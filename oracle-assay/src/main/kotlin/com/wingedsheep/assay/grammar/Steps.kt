package com.wingedsheep.assay.grammar

import com.wingedsheep.assay.normalize.Normalizer
import com.wingedsheep.assay.syntax.Phrase
import com.wingedsheep.assay.syntax.alternate
import com.wingedsheep.assay.syntax.bind
import com.wingedsheep.assay.syntax.oneOf
import com.wingedsheep.assay.syntax.phrase
import com.wingedsheep.assay.syntax.separated
import com.wingedsheep.sdk.core.AbilityFlag
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets as SdkTargets
import com.wingedsheep.sdk.model.CardScript
import com.wingedsheep.sdk.scripting.conditions.Condition
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.CompositeEffect
import com.wingedsheep.sdk.scripting.effects.CreateDelayedTriggerEffect
import com.wingedsheep.sdk.scripting.effects.FlipCoinEffect
import com.wingedsheep.sdk.scripting.effects.ForEachPlayerEffect
import com.wingedsheep.sdk.scripting.effects.ForceSacrificeEffect
import com.wingedsheep.sdk.scripting.effects.MayPayManaEffect
import com.wingedsheep.sdk.scripting.effects.PayManaCostEffect
import com.wingedsheep.sdk.scripting.effects.MayPayXForEffect
import com.wingedsheep.sdk.scripting.effects.RedirectNextDamageEffect
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect
import com.wingedsheep.sdk.scripting.effects.DealDamageEffect
import com.wingedsheep.sdk.scripting.effects.DrawCardsEffect
import com.wingedsheep.sdk.scripting.effects.Effect
import com.wingedsheep.sdk.scripting.effects.ForEachTargetEffect
import com.wingedsheep.sdk.scripting.effects.GainLifeEffect
import com.wingedsheep.sdk.scripting.effects.GrantKeywordEffect
import com.wingedsheep.sdk.scripting.effects.PreventDamageEffect
import com.wingedsheep.sdk.scripting.effects.RegenerateEffect
import com.wingedsheep.sdk.scripting.effects.SacrificeEffect
import com.wingedsheep.sdk.scripting.effects.Gate
import com.wingedsheep.sdk.scripting.effects.GatedEffect
import com.wingedsheep.sdk.scripting.effects.LoseLifeEffect
import com.wingedsheep.sdk.scripting.effects.MayEffect
import com.wingedsheep.sdk.scripting.effects.ModifyStatsEffect
import com.wingedsheep.sdk.scripting.effects.PlayAdditionalLandsEffect
import com.wingedsheep.sdk.scripting.effects.ScryEffect
import com.wingedsheep.sdk.scripting.effects.SurveilEffect
import com.wingedsheep.sdk.scripting.effects.TakeExtraTurnEffect
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetCreature
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.scripting.targets.TargetObject
import com.wingedsheep.sdk.scripting.targets.TargetRequirement
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * The steps a spell performs — the pipeline family, and the rules that produce a `CardScript`
 * rather than a keyword.
 *
 * Each rule targets `mtg-sdk` through its companion facade ([Effects], `Patterns`) rather than a raw
 * constructor, matching the discipline `FacadeBoundaryTest` enforces for cards. A rule's `match`
 * half necessarily destructures the concrete effect class, since that is the only way to read a
 * model back; the asymmetry is inherent to a bidirectional rule and is why `build` going through the
 * facade matters — it is the half that would otherwise drift from how cards are written.
 *
 * ## A clause, a sentence, and a line
 *
 * The unit the rules below are written in is the **clause** — the verb phrase with no full stop on
 * it. A [sentence] is a clause plus its stop, and a [step] is either one sentence or a [sequence] of
 * them. That three-way split is what lets a card printing two sentences on one line
 * ("Target creature gets +1/+3 until end of turn. Untap that creature.") reuse the ordinary effect
 * vocabulary twice instead of needing a second, capitalized copy of every verb: a full stop is a
 * sentence start, which [com.wingedsheep.assay.syntax.SentenceCase] owns, so every template here is
 * written mid-sentence exactly as the keyword rules spell themselves "flying" rather than "Flying".
 *
 * Everything outside this file slots [step], so a trigger, an activated ability and a spell line all
 * gained sequences at once and none of them had to be told.
 *
 * ## Singular and plural are separate rules
 *
 * "Draw a card." and "Draw two cards." differ in the article *and* the noun, so one template cannot
 * spell both. They are therefore two rules over disjoint counts — [Cardinals.word] starts at two and
 * the singular rule is the only one that builds 1 — which keeps exactly one printed form per model
 * and leaves nothing for the printer to choose. Overlapping them would be an ambiguity hard error on
 * every draw card in the corpus, which is the grammar telling the truth about a bad factoring.
 */
object Steps {

    // ---------------------------------------------------------------------------------------
    // Draw
    // ---------------------------------------------------------------------------------------

    private val drawOne: Phrase<CardScript> = phrase("draw a card", name = "draw a card") {
        build { CardScript(spellEffect = Effects.DrawCards(1)) }
        match { script -> if (drawnByController(script) == 1) bind() else null }
    }

    private val drawMany: Phrase<CardScript> = phrase("draw {n} cards", name = "draw cards") {
        slot("n", Cardinals.word)
        build { CardScript(spellEffect = Effects.DrawCards(it.int("n"))) }
        match { script ->
            val count = drawnByController(script) ?: return@match null
            // The singular is drawOne's to print, and anything Cardinals cannot spell as a word has
            // no surface form here at all. Refusing both is what keeps printing total-or-null
            // rather than total-or-wrong.
            if (count >= 2 && Cardinals.spellable(count)) bind("n" to count) else null
        }
    }

    private val targetPlayerDrawsOne: Phrase<CardScript> =
        phrase("target player draws a card", name = "target player draws a card") {
            build { targetPlayerDraws(1) }
            match { script -> if (drawnByTarget(script) == 1) bind() else null }
        }

    private val targetPlayerDrawsMany: Phrase<CardScript> =
        phrase("target player draws {n} cards", name = "target player draws cards") {
            slot("n", Cardinals.word)
            build { targetPlayerDraws(it.int("n")) }
            match { script ->
                val count = drawnByTarget(script) ?: return@match null
                if (count >= 2 && Cardinals.spellable(count)) bind("n" to count) else null
            }
        }

    // ---------------------------------------------------------------------------------------
    // One permanent, one verb
    // ---------------------------------------------------------------------------------------

    /**
     * The shape shared by "Destroy target creature.", "Exile target artifact.", "Tap target
     * creature you control." — a verb, one targeted permanent, and nothing else.
     *
     * The `match` half is an **equality test against what `build` would have produced**, not a
     * structural walk. That is deliberate and it is the discipline the whole file follows: a matcher
     * that inspected only the fields it cared about would happily print a script carrying extra
     * content it never looked at, which round-trips and loses meaning — the reversible-but-wrong
     * class. Reconstructing the whole script and comparing makes the check exhaustive by
     * construction, so a rule cannot fall behind the effect it prints.
     */
    private fun targetedPermanentStep(
        template: String,
        name: String,
        effect: (EffectTarget) -> Effect,
    ): Phrase<CardScript> {
        fun scriptFor(filter: GameObjectFilter) = CardScript(
            spellEffect = effect(Targets.bound()),
            targetRequirements = listOf(Targets.permanent(filter)),
        )
        return phrase(template, name = name) {
            slot("filter", Filters.filter)
            build { scriptFor(it.value("filter")) }
            match { script ->
                val requirement = script.targetRequirements.singleOrNull() ?: return@match null
                val filter = Targets.permanentFilter(requirement) ?: return@match null
                if (script != scriptFor(filter)) return@match null
                bind("filter" to filter)
            }
        }
    }

    // ---------------------------------------------------------------------------------------
    // Counted verbs — "scry 2", "you gain 3 life", "target player gains 3 life"
    // ---------------------------------------------------------------------------------------

    /**
     * A verb whose only variable is a number written in **digits**.
     *
     * Digits, not [Cardinals.word]: Oracle spells a *quantity of cards* as a word ("draw two cards")
     * and a *quantity of life, damage or counters* as a numeral ("you gain 3 life", "deals 2
     * damage"). The two are different conventions in the same text, which is why the draw rules
     * above take one leaf and these take the other — and why neither can borrow the other's, in
     * either direction.
     *
     * The shape takes both halves of the inversion explicitly. `script` is the forward direction and
     * `count` reads the number back out of the effect, because there is no general way to invert an
     * arbitrary builder. Everything *else* the script might carry is still checked the fail-closed
     * way the rest of this file is: `count` only recovers the number, and the equality against
     * `script(n)` is what refuses to print a script carrying anything the sentence does not say.
     */
    private fun countedStep(
        template: String,
        name: String,
        script: (Int) -> CardScript,
        count: (Effect) -> Int?,
    ): Phrase<CardScript> = phrase(template, name = name) {
        slot("n", Primitives.cardinal)
        if (template.contains("{self}")) slot("self", Primitives.self)
        build { script(it.int("n")) }
        match { model ->
            val amount = count(model.spellEffect ?: return@match null) ?: return@match null
            if (model != script(amount)) return@match null
            bind("n" to amount, "self" to Unit)
        }
    }

    /**
     * The same shape over a [DynamicAmount] the text names in words rather than in digits — "deals
     * **X** damage", "gains life equal to the number of Mountains you control".
     *
     * Kept apart from [countedStep] rather than generalized over the amount, because the *printed
     * form* of the amount is not a slot at all in these: "X" is a literal, and "equal to the number
     * of …" is a whole clause. What varies is which amount the template denotes, so the amount is a
     * parameter of the rule and not of the sentence.
     */
    private fun amountStep(
        template: String,
        name: String,
        amount: DynamicAmount,
        script: (DynamicAmount) -> CardScript,
    ): Phrase<CardScript> = phrase(template, name = name) {
        if (template.contains("{self}")) slot("self", Primitives.self)
        build { script(amount) }
        match { if (it == script(amount)) bind("self" to Unit) else null }
    }

    /**
     * The same shape with "may" inserted after the clause's own subject — "You **may** gain 3 life."
     *
     * [mayClause] cannot reach these. It spells "you may {inner}" over a clause that states no
     * subject of its own ("draw a card"), and a clause that states "you" would come back as "you may
     * you gain 3 life": English contracts the wrapper's subject with the clause's, and the model is
     * the same `MayEffect` either way. So the contraction is a printed-shape fact, and it is written
     * as a *variant of the same shape* rather than as a rule of its own — one call site per clause,
     * with both spellings generated from one template, which is what stops the two drifting.
     */
    private fun mayCountedStep(
        template: String,
        name: String,
        script: (Int) -> CardScript,
        count: (Effect) -> Int?,
    ): Phrase<CardScript> = countedStep(
        template,
        name,
        script = { amount -> wrap(script(amount)) { MayEffect(it) } ?: script(amount) },
        count = { effect ->
            val gated = effect as? GatedEffect
            if (gated == null || gated.gate !is Gate.MayDecide || gated != MayEffect(gated.then)) {
                null
            } else {
                count(gated.then)
            }
        },
    )

    private val countedSteps: List<Phrase<CardScript>> = listOf(
        countedStep(
            "you gain {n} life", "you gain life",
            script = { CardScript(spellEffect = Effects.GainLife(it)) },
            count = ::lifeGained,
        ),
        mayCountedStep(
            "you may gain {n} life", "you may gain life",
            script = { CardScript(spellEffect = Effects.GainLife(it)) },
            count = ::lifeGained,
        ),
        countedStep(
            "target player gains {n} life", "target player gains life",
            script = {
                CardScript(
                    spellEffect = Effects.GainLife(it, Targets.bound()),
                    targetRequirements = listOf(Targets.player()),
                )
            },
            count = ::lifeGained,
        ),
        countedStep(
            "you lose {n} life", "you lose life",
            script = { CardScript(spellEffect = Effects.LoseLife(it, EffectTarget.Controller)) },
            count = ::lifeLost,
        ),
        countedStep(
            "target player loses {n} life", "target player loses life",
            script = {
                CardScript(
                    spellEffect = Effects.LoseLife(it, Targets.bound()),
                    targetRequirements = listOf(Targets.player()),
                )
            },
            count = ::lifeLost,
        ),
        countedStep(
            "scry {n}", "scry",
            script = { CardScript(spellEffect = Effects.Scry(it)) },
            count = { (it as? ScryEffect)?.count },
        ),
        countedStep(
            "surveil {n}", "surveil",
            script = { CardScript(spellEffect = Effects.Surveil(it)) },
            count = { (it as? SurveilEffect)?.count },
        ),
        countedStep(
            "{self} deals {n} damage to any target", "deals damage to any target",
            script = {
                CardScript(
                    spellEffect = Effects.DealDamage(it, Targets.bound()),
                    targetRequirements = listOf(Targets.any()),
                )
            },
            count = ::damageDealt,
        ),
        // Lavaborn Muse. "That player" is the one whose step triggered, which the model names
        // directly — so unlike "target player" this clause declares no requirement at all.
        countedStep(
            "{self} deals {n} damage to that player", "deals damage to the triggering player",
            script = {
                CardScript(
                    spellEffect = Effects.DealDamage(it, EffectTarget.PlayerRef(Player.TriggeringPlayer))
                )
            },
            count = ::damageDealt,
        ),
        countedStep(
            "{self} deals {n} damage to target player", "deals damage to target player",
            script = {
                CardScript(
                    spellEffect = Effects.DealDamage(it, Targets.bound()),
                    targetRequirements = listOf(Targets.player()),
                )
            },
            count = ::damageDealt,
        ),
        // "Target opponent or planeswalker" is the modern redirection wording, and it is a
        // requirement type of its own rather than a filter — so it is a row beside "target player"
        // rather than a case inside it.
        countedStep(
            "{self} deals {n} damage to target opponent or planeswalker",
            "deals damage to target opponent or planeswalker",
            script = {
                CardScript(
                    spellEffect = Effects.DealDamage(it, Targets.bound()),
                    targetRequirements = listOf(Targets.opponentOrPlaneswalker()),
                )
            },
            count = ::damageDealt,
        ),
        countedStep(
            "{self} deals {n} damage to target player or planeswalker",
            "deals damage to target player or planeswalker",
            script = {
                CardScript(
                    spellEffect = Effects.DealDamage(it, Targets.bound()),
                    targetRequirements = listOf(Targets.playerOrPlaneswalker()),
                )
            },
            count = ::damageDealt,
        ),
    )

    /**
     * The clauses whose whole sentence is one published effect and whose only variable, if any, is a
     * noun phrase the ordinary vocabularies already spell.
     *
     * Each unlocks a single card, which the module's rule says needs a stated reason, and the reason
     * is the same for every one: the *model* is a single effect type or a single published
     * `Patterns` recipe, so the sentence is the unit and there is no smaller rule to write. A shape
     * parameterized over them would be a factory with one member each.
     */
    private val sentenceClauses: List<Phrase<CardScript>> = listOf(
        // Unstable Hulk's drawback, and the only turn-skipping sentence in the set.
        constantClause("you skip your next turn", "you skip your next turn", Effects.SkipNextTurn()),
        // Willbender. `Targets.SpellOrAbilityWithSingleTarget` is a whole requirement rather than a
        // filter — a spell *or* an ability is not an object the noun-phrase cascade can name — so
        // the requirement is slotted verbatim and the effect reads nothing from it.
        run {
            val script = CardScript(
                spellEffect = Effects.ChangeTarget(),
                targetRequirements = listOf(SdkTargets.SpellOrAbilityWithSingleTarget),
            )
            phrase<CardScript>(
                "change the target of target spell or ability with a single target",
                name = "change a spell's target",
            ) {
                build { script }
                match { if (it == script) bind() else null }
            }
        },
        // Planar Guide. Two printed sentences and one recipe: the exile *links* the cards it removed
        // and the delayed trigger returns that link, so the second sentence's "those cards" is the
        // first sentence's slot and neither half denotes anything alone.
        run {
            val script = CardScript(
                spellEffect = Effects.ExileGroupAndLink(GroupFilter.AllCreatures).then(
                    CreateDelayedTriggerEffect(
                        step = Step.END,
                        effect = Effects.ReturnLinkedExileUnderOwnersControl(),
                    )
                )
            )
            phrase<CardScript>(
                "exile all creatures. at the beginning of the next end step, return those cards to " +
                    "the battlefield under their owners' control",
                name = "exile all creatures and return them",
            ) {
                build { script }
                match { if (it == script) bind() else null }
            }
        },
        // Goblin Assassin. "A creature of their choice" is what an edict aimed at every player means,
        // and `ForEachPlayerEffect` rebinding the controller per player is what makes "their" work —
        // which is why the inner sacrifice names `Controller` rather than a player reference.
        run {
            val script = CardScript(
                spellEffect = ForEachPlayerEffect(
                    players = Player.Each,
                    effects = listOf(
                        FlipCoinEffect(
                            lostEffect = ForceSacrificeEffect(
                                filter = GameObjectFilter.Creature,
                                count = 1,
                                target = EffectTarget.Controller,
                            )
                        )
                    ),
                )
            )
            phrase<CardScript>(
                "each player flips a coin. each player whose coin comes up tails sacrifices a " +
                    "creature of their choice",
                name = "each player flips a coin and may sacrifice",
            ) {
                build { script }
                match { if (it == script) bind() else null }
            }
        },
        // Beacon of Destiny. One effect with two references and no variable: the damage you would
        // take is redirected to the source.
        run {
            val script = CardScript(
                spellEffect = RedirectNextDamageEffect(
                    protectedTargets = listOf(EffectTarget.Controller),
                    redirectTo = EffectTarget.Self,
                )
            )
            phrase<CardScript>(
                "the next time a source of your choice would deal damage to you this turn, that " +
                    "damage is dealt to {self} instead",
                name = "redirect the next damage to the source",
            ) {
                slot("self", Primitives.self)
                build { script }
                match { if (it == script) bind("self" to Unit) else null }
            }
        },
        // Riptide Mangler. The target is *read* rather than acted on — the effect sets the source's
        // base power to the chosen creature's — so the requirement is declared and the reference in
        // the effect is a `targetPower`, not a bound variable.
        run {
            fun scriptFor(filter: GameObjectFilter) = CardScript(
                spellEffect = Effects.SetBasePower(
                    target = EffectTarget.Self,
                    power = DynamicAmounts.targetPower(0),
                ),
                targetRequirements = listOf(Targets.permanent(filter)),
            )
            phrase<CardScript>(
                "change {self}'s base power to target {filter}'s power",
                name = "set the source's base power to a target's",
            ) {
                slot("self", Primitives.self)
                slot("filter", Filters.filter)
                build { scriptFor(it.value("filter")) }
                match { script ->
                    val requirement = script.targetRequirements.singleOrNull() ?: return@match null
                    val filter = Targets.permanentFilter(requirement) ?: return@match null
                    if (script != scriptFor(filter)) return@match null
                    bind("self" to Unit, "filter" to filter)
                }
            }
        },
    )

    /** A whole sentence that denotes one fixed effect and nothing else. */
    private fun constantClause(template: String, name: String, effect: Effect): Phrase<CardScript> {
        val script = CardScript(spellEffect = effect)
        return phrase(template, name = name) {
            build { script }
            match { if (it == script) bind() else null }
        }
    }

    /**
     * "You may exchange control of target creature you control and target creature an opponent
     * controls." — Chromeshell Crab, and the first rule in the grammar that declares **two** targets.
     *
     * The two requirements are named by [Targets.slot], which is what [merge]'s KDoc says the
     * missing piece was: a single fixed slot name is enough only while every rule takes at most one
     * target, and a rule that took two would otherwise produce a script with two requirements both
     * called `target`. Numbering them is the whole fix, and the differential compares slots by
     * position so nothing downstream sees the names.
     */
    private val exchangeControl: Phrase<CardScript> = run {
        fun scriptFor(mine: GameObjectFilter, theirs: GameObjectFilter) = CardScript(
            spellEffect = MayEffect(Effects.ExchangeControl(Targets.bound(0), Targets.bound(1))),
            targetRequirements = listOf(Targets.permanent(mine, 0), Targets.permanent(theirs, 1)),
        )
        phrase("you may exchange control of target {mine} and target {theirs}", name = "exchange control") {
            slot("mine", Filters.filter)
            slot("theirs", Filters.filter)
            build { scriptFor(it.value("mine"), it.value("theirs")) }
            match { script ->
                if (script.targetRequirements.size != 2) return@match null
                val mine = Targets.permanentFilter(script.targetRequirements[0]) ?: return@match null
                val theirs = (script.targetRequirements[1] as? TargetObject)?.filter?.baseFilter
                    ?: return@match null
                if (script != scriptFor(mine, theirs)) return@match null
                bind("mine" to mine, "theirs" to theirs)
            }
        }
    }

    /**
     * The one-off clauses: a whole printed sentence that denotes one published effect, with at most
     * one number in it.
     *
     * Each unlocks a single card today, which the module's own rule says needs a stated reason. The
     * reason is the same for all four: the *model* is one effect type with one field, so there is no
     * smaller rule to write — the sentence is the unit, and a shape parameterized over four
     * unrelated effect types would be a factory with one member each.
     */
    private val turnSteps: List<Phrase<CardScript>> = listOf(
        // "You may play up to three additional lands this turn." — Summer Bloom. The count is a
        // word rather than a numeral, so it takes Cardinals rather than [countedStep]'s digit leaf.
        run {
            fun scriptFor(count: Int) = CardScript(spellEffect = PlayAdditionalLandsEffect(count))
            phrase("you may play up to {n} additional lands this turn", name = "play additional lands") {
                slot("n", Cardinals.word)
                build { scriptFor(it.int("n")) }
                match { script ->
                    val count = (script.spellEffect as? PlayAdditionalLandsEffect)?.count ?: return@match null
                    if (!Cardinals.spellable(count) || script != scriptFor(count)) return@match null
                    bind("n" to count)
                }
            }
        },
        // "Take an extra turn after this one. At the beginning of that turn's end step, you lose the
        // game." — Last Chance. Two printed sentences and one model: `loseAtEndStep` is a field on
        // the extra turn rather than a second effect, so there is nothing for [sequenceClause] to
        // split and the rule spans both sentences.
        run {
            val script = CardScript(spellEffect = TakeExtraTurnEffect(loseAtEndStep = true))
            phrase(
                "take an extra turn after this one. at the beginning of that turn's end step, you lose the game",
                name = "take an extra turn and lose",
            ) {
                build { script }
                match { if (it == script) bind() else null }
            }
        },
        // "You lose half your life, rounded up." — Cruel Bargain's second sentence.
        run {
            val script = CardScript(
                spellEffect = Effects.LoseLife(
                    DynamicAmount.Divide(DynamicAmount.LifeTotal(Player.You), DynamicAmount.Fixed(2), roundUp = true),
                    EffectTarget.Controller,
                )
            )
            phrase("you lose half your life, rounded up", name = "lose half your life") {
                build { script }
                match { if (it == script) bind() else null }
            }
        },
        // "If target opponent has more cards in hand than you, draw cards equal to the difference." —
        // Balance of Power. The "if" is inside the amount (`IfPositive` around a subtraction) rather
        // than around the effect, so this is one clause and not [conditionalClause]'s shape.
        run {
            val script = CardScript(
                spellEffect = Effects.DrawCards(DynamicAmounts.handSizeDifferenceFromTargetOpponent()),
                targetRequirements = listOf(Targets.opponent()),
            )
            phrase(
                "if target opponent has more cards in hand than you, draw cards equal to the difference",
                name = "draw the hand-size difference",
            ) {
                build { script }
                match { if (it == script) bind() else null }
            }
        },
    )

    // ---------------------------------------------------------------------------------------
    // A count and a filtered target together
    // ---------------------------------------------------------------------------------------

    /**
     * "~ deals 2 damage to target creature." — the same verb as above over a noun phrase rather than
     * over the fixed "any target" / "target player" forms, so it carries two slots instead of one.
     */
    private val damageToTargetPermanent: Phrase<CardScript> = run {
        fun scriptFor(amount: Int, filter: GameObjectFilter) = CardScript(
            spellEffect = Effects.DealDamage(amount, Targets.bound()),
            targetRequirements = listOf(Targets.permanent(filter)),
        )
        phrase(
            "{self} deals {n} damage to target {filter}",
            name = "deals damage to target permanent",
        ) {
            slot("self", Primitives.self)
            slot("n", Primitives.cardinal)
            slot("filter", Filters.filter)
            build { scriptFor(it.int("n"), it.value("filter")) }
            match { script ->
                val amount = damageDealt(script.spellEffect ?: return@match null) ?: return@match null
                val requirement = script.targetRequirements.singleOrNull() ?: return@match null
                val filter = Targets.permanentFilter(requirement) ?: return@match null
                if (script != scriptFor(amount, filter)) return@match null
                bind("self" to Unit, "n" to amount, "filter" to filter)
            }
        }
    }

    /**
     * "Target creature gets +3/+3 until end of turn." — the pump spell.
     *
     * The duration is spelled by the template and *not* by a slot: `Duration.EndOfTurn` is
     * `ModifyStats`'s default, and every other duration the SDK has ("as long as", "until your next
     * turn", `WhileSourceTapped`) is a different sentence rather than a different word in this one.
     * The reconstruct-and-compare in `match` is what makes that safe — a script whose duration is
     * anything else refuses to print here rather than losing the distinction.
     */
    private val pumpTargetPermanent: Phrase<CardScript> = run {
        fun scriptFor(modifiers: Pair<Int, Int>, filter: GameObjectFilter) = CardScript(
            spellEffect = Effects.ModifyStats(modifiers.first, modifiers.second, Targets.bound()),
            targetRequirements = listOf(Targets.permanent(filter)),
        )
        phrase("target {filter} gets {mod} until end of turn", name = "pump target") {
            slot("filter", Filters.filter)
            slot("mod", Primitives.statModifiers)
            build { scriptFor(it.value("mod"), it.value("filter")) }
            match { script ->
                val modifiers = fixedModifiers(script.spellEffect) ?: return@match null
                val requirement = script.targetRequirements.singleOrNull() ?: return@match null
                val filter = Targets.permanentFilter(requirement) ?: return@match null
                if (script != scriptFor(modifiers, filter)) return@match null
                bind("filter" to filter, "mod" to modifiers)
            }
        }
    }

    /** "Target creature gains flying until end of turn." — the pump rule's keyword sibling. */
    private val grantToTargetPermanent: Phrase<CardScript> = run {
        fun scriptFor(keyword: Keyword, filter: GameObjectFilter) = CardScript(
            spellEffect = Effects.GrantKeyword(keyword, Targets.bound()),
            targetRequirements = listOf(Targets.permanent(filter)),
        )
        phrase("target {filter} gains {kw} until end of turn", name = "grant a keyword to a target") {
            slot("filter", Filters.filter)
            slot("kw", Keywords.keyword)
            build { scriptFor(it.value("kw"), it.value("filter")) }
            match { script ->
                val keyword = grantedKeyword(script.spellEffect) ?: return@match null
                val requirement = script.targetRequirements.singleOrNull() ?: return@match null
                val filter = Targets.permanentFilter(requirement) ?: return@match null
                if (script != scriptFor(keyword, filter)) return@match null
                bind("filter" to filter, "kw" to keyword)
            }
        }
    }

    /**
     * "Target creature gets +3/+3 and gains flying until end of turn." — Angelic Blessing.
     *
     * One sentence, one target, **two** effects, which is why it is a rule of its own rather than a
     * [sequence]: the second clause has no subject of its own in the text, and the model shares one
     * requirement between the two effects. [Statics.pumpAndKeyword] is the same shape on the static
     * side, and the answer is the same — the model is already right, and what a compound SDK type
     * would buy is nothing.
     */
    private val pumpAndGrantTarget: Phrase<CardScript> = run {
        fun scriptFor(modifiers: Pair<Int, Int>, keyword: Keyword, filter: GameObjectFilter) = CardScript(
            spellEffect = Effects.Composite(
                listOf(
                    Effects.ModifyStats(modifiers.first, modifiers.second, Targets.bound()),
                    Effects.GrantKeyword(keyword, Targets.bound()),
                )
            ),
            targetRequirements = listOf(Targets.permanent(filter)),
        )
        phrase(
            "target {filter} gets {mod} and gains {kw} until end of turn",
            name = "pump and grant a keyword to a target",
        ) {
            slot("filter", Filters.filter)
            slot("mod", Primitives.statModifiers)
            slot("kw", Keywords.keyword)
            build { scriptFor(it.value("mod"), it.value("kw"), it.value("filter")) }
            match { script ->
                val effects = (script.spellEffect as? CompositeEffect)?.effects ?: return@match null
                val modifiers = fixedModifiers(effects.firstOrNull()) ?: return@match null
                val keyword = grantedKeyword(effects.getOrNull(1)) ?: return@match null
                val requirement = script.targetRequirements.singleOrNull() ?: return@match null
                val filter = Targets.permanentFilter(requirement) ?: return@match null
                if (script != scriptFor(modifiers, keyword, filter)) return@match null
                bind("filter" to filter, "mod" to modifiers, "kw" to keyword)
            }
        }
    }

    /**
     * "Destroy two target lands.", "Tap up to three target creatures without flying." — a verb over
     * *several* targets, which the SDK spells as one requirement with a count and a
     * `ForEachTargetEffect` over `ContextTarget(0)`.
     *
     * Positional rather than named for [Combat.returnOneOrTwoTargets]'s reason: the iteration
     * rebinds slot 0 per target, so a named reference would name the whole declaration.
     *
     * "Up to three" and "three" differ in one field — `optional` on the requirement — and in one
     * word, so they are two rows of this shape rather than one rule with an optional literal.
     */
    private fun multiTargetStep(
        template: String,
        name: String,
        optional: Boolean,
        member: (EffectTarget) -> Effect,
    ): Phrase<CardScript> {
        fun scriptFor(count: Int, filter: GameObjectFilter) = CardScript(
            spellEffect = ForEachTargetEffect(listOf(member(EffectTarget.ContextTarget(0)))),
            targetRequirements = listOf(
                TargetCreature(
                    count = count,
                    optional = optional,
                    filter = TargetFilter(filter),
                    id = Targets.SLOT,
                )
            ),
        )
        return phrase(template, name = name) {
            slot("n", Cardinals.word)
            slot("filter", Filters.plural)
            build { scriptFor(it.int("n"), it.value("filter")) }
            match { script ->
                val requirement = script.targetRequirements.singleOrNull() as? TargetObject ?: return@match null
                val filter = requirement.filter.baseFilter
                if (!Cardinals.spellable(requirement.count)) return@match null
                if (script != scriptFor(requirement.count, filter)) return@match null
                bind("n" to requirement.count, "filter" to filter)
            }
        }
    }

    /**
     * "Destroy target nonblack creature. It can't be regenerated." — Skinthinner, Deathmark Prelate.
     *
     * Two printed sentences and one rule, for [destroyAllNoRegenerate]'s reason: `noRegenerate` is a
     * *marker effect placed before the destroy* rather than a second sentence's worth of behaviour,
     * so there is nothing for [sequenceClause] to split and the order in the model is the reverse of
     * the order in the text. `Effects.Destroy(target, noRegenerate = true)` composes exactly that
     * pair, which is why this goes through the facade rather than assembling it.
     */
    private val destroyTargetNoRegenerate: Phrase<CardScript> = run {
        fun scriptFor(filter: GameObjectFilter) = CardScript(
            spellEffect = Effects.Destroy(Targets.bound(), noRegenerate = true),
            targetRequirements = listOf(Targets.permanent(filter)),
        )
        phrase("destroy target {filter}. it can't be regenerated", name = "destroy target without regeneration") {
            slot("filter", Filters.filter)
            build { scriptFor(it.value("filter")) }
            match { script ->
                val requirement = script.targetRequirements.singleOrNull() ?: return@match null
                val filter = Targets.permanentFilter(requirement) ?: return@match null
                if (script != scriptFor(filter)) return@match null
                bind("filter" to filter)
            }
        }
    }

    /**
     * "Destroy that creature. It can't be regenerated." — Dripping Dead, Phage, Toxin Sliver.
     *
     * "That creature" here is the creature the *trigger* named, not a target the spell chose, which
     * is a third anaphor beside [SelfSteps]' "it" and [Continuations]' "that creature": the sentence
     * follows "Whenever ~ deals combat damage to a creature", and the model says
     * `EffectTarget.TriggeringEntity`. It is reachable as an ordinary first clause because it
     * introduces nothing — the trigger already did — and it cannot collide with [Continuations],
     * which is only reachable from a *later* clause position.
     */
    private val destroyTriggeringNoRegenerate: Phrase<CardScript> = run {
        val script = CardScript(
            spellEffect = Effects.Destroy(EffectTarget.TriggeringEntity, noRegenerate = true)
        )
        phrase(
            "destroy that creature. it can't be regenerated",
            name = "destroy the triggering creature without regeneration",
        ) {
            build { script }
            match { if (it == script) bind() else null }
        }
    }

    /**
     * "Sacrifice a permanent.", "Sacrifice a land." — the controller sacrifices, with no target.
     *
     * `SacrificeEffect` carries no player at all: the ability's controller is the one who
     * sacrifices, which is what the bare imperative means. `Effects.Sacrifice(filter, 1, target)` is
     * a *different* type (`ForceSacrificeEffect`) naming a player, and the two are a "one concept,
     * two spellings" pair the corpus is split over — Drinker of Sorrow writes the first and Goblin
     * Firebug the second. The grammar emits the one whose model says what the sentence says and lets
     * the differential report the rest, per the module's rule for two SDK spellings.
     */
    private val sacrificeFiltered: Phrase<CardScript> = run {
        fun scriptFor(filter: GameObjectFilter) = CardScript(spellEffect = SacrificeEffect(filter))
        phrase("sacrifice {filter}", name = "sacrifice a permanent") {
            slot("filter", Filters.indefinite)
            build { scriptFor(it.value("filter")) }
            match { script ->
                val effect = script.spellEffect as? SacrificeEffect ?: return@match null
                if (script != scriptFor(effect.filter)) return@match null
                bind("filter" to effect.filter)
            }
        }
    }

    /**
     * "Target creature can't be blocked this turn." — Cephalid Pathmage.
     *
     * The SDK grants the *flag* rather than a keyword here, which is the same two-places-for-one-
     * thing finding [Grammar.flagLine] records: `AbilityFlag.CANT_BE_BLOCKED` is a card-level flag
     * for the permanent form and a `GrantKeywordEffect` over the flag's own name for the durational
     * one. The rule spells the flag's name because that is what the cards carry.
     */
    private val targetCantBeBlocked: Phrase<CardScript> = run {
        fun scriptFor(filter: GameObjectFilter) = CardScript(
            spellEffect = GrantKeywordEffect(AbilityFlag.CANT_BE_BLOCKED.name, Targets.bound()),
            targetRequirements = listOf(Targets.permanent(filter)),
        )
        phrase("target {filter} can't be blocked this turn", name = "target can't be blocked") {
            slot("filter", Filters.filter)
            build { scriptFor(it.value("filter")) }
            match { script ->
                val requirement = script.targetRequirements.singleOrNull() ?: return@match null
                val filter = Targets.permanentFilter(requirement) ?: return@match null
                if (script != scriptFor(filter)) return@match null
                bind("filter" to filter)
            }
        }
    }

    /**
     * "Prevent the next 2 damage that would be dealt to any target this turn." — Aven Redeemer —
     * and "Prevent all combat damage that would be dealt to and dealt by ~ this turn." — Deftblade
     * Elite.
     *
     * One SDK type with two very different sentences: a counted prevention aimed at a target, and an
     * uncounted one scoped to combat and to both directions. They share nothing but the verb, so
     * they are two rules rather than a shape with a slot.
     */
    private val preventNextDamage: Phrase<CardScript> = run {
        fun scriptFor(amount: Int) = CardScript(
            spellEffect = Effects.PreventNextDamage(amount, Targets.bound()),
            targetRequirements = listOf(Targets.any()),
        )
        phrase(
            "prevent the next {n} damage that would be dealt to any target this turn",
            name = "prevent the next damage to any target",
        ) {
            slot("n", Primitives.cardinal)
            build { scriptFor(it.int("n")) }
            match { script ->
                val prevented = (script.spellEffect as? PreventDamageEffect)?.amount
                    ?.let { it as? DynamicAmount.Fixed }?.amount ?: return@match null
                if (script != scriptFor(prevented)) return@match null
                bind("n" to prevented)
            }
        }
    }

    private val preventCombatDamageToAndBySelf: Phrase<CardScript> = run {
        val script = CardScript(spellEffect = Effects.PreventCombatDamageToAndBy(EffectTarget.Self))
        phrase(
            "prevent all combat damage that would be dealt to and dealt by {self} this turn",
            name = "prevent combat damage to and by the source",
        ) {
            slot("self", Primitives.self)
            build { script }
            match { if (it == script) bind("self" to Unit) else null }
        }
    }

    /** "You lose the game." / "That player loses the game." — Phage the Untouchable, both halves. */
    private fun losesTheGame(template: String, name: String, player: EffectTarget): Phrase<CardScript> {
        val script = CardScript(spellEffect = Effects.LoseGame(player))
        return phrase(template, name = name) {
            build { script }
            match { if (it == script) bind() else null }
        }
    }

    private val permanentSteps: List<Phrase<CardScript>> = listOf(
        targetedPermanentStep("destroy target {filter}", "destroy target") { Effects.Destroy(it) },
        targetedPermanentStep("regenerate target {filter}", "regenerate target") { RegenerateEffect(it) },
        destroyTargetNoRegenerate,
        destroyTriggeringNoRegenerate,
        sacrificeFiltered,
        targetCantBeBlocked,
        preventNextDamage,
        preventCombatDamageToAndBySelf,
        losesTheGame("you lose the game", "you lose the game", EffectTarget.Controller),
        losesTheGame(
            "that player loses the game",
            "the triggering player loses the game",
            EffectTarget.PlayerRef(Player.TriggeringPlayer),
        ),
        targetedPermanentStep("exile target {filter}", "exile target") { Effects.Exile(it) },
        targetedPermanentStep("tap target {filter}", "tap target") { Effects.Tap(it) },
        targetedPermanentStep("untap target {filter}", "untap target") { Effects.Untap(it) },
        targetedPermanentStep(
            "return target {filter} to its owner's hand",
            "return target to hand",
        ) { Effects.ReturnToHand(it) },
        targetedPermanentStep(
            "put target {filter} on top of its owner's library",
            "put target on top of its library",
        ) { Effects.PutOnTopOfLibrary(it) },
        multiTargetStep("destroy {n} target {filter}", "destroy several targets", optional = false) {
            Effects.Destroy(it)
        },
        multiTargetStep("tap up to {n} target {filter}", "tap up to several targets", optional = true) {
            Effects.Tap(it)
        },
    )

    // ---------------------------------------------------------------------------------------
    // Whole groups — "Creatures you control get +1/+1", "Destroy all white creatures"
    // ---------------------------------------------------------------------------------------

    /**
     * The mass effects, which the SDK spells as one iteration over a `GroupFilter` with the
     * per-member effect written against [EffectTarget.Self].
     *
     * One shape, four surfaces, because English gives the same model four templates and the
     * difference between them is the *noun phrase*, not the verb: a bare plural subject ("Creatures
     * you control get …"), "all" plus a plural ("Destroy all white creatures"), and "each" plus a
     * singular ("deals 1 damage to each attacking creature"). Which one a card prints is a fact
     * about the sentence's shape rather than about the group, so the templates are enumerated and
     * the group filter is [Filters.plural] or [Filters.filter] slotted whole.
     *
     * `GroupFilter(filter)` and nothing else: `excludeSelf`, `excludeTarget`, a non-battlefield
     * scope and `noRegenerate` all say things these sentences do not, and the reconstruct-and-
     * compare refuses to print a value carrying any of them.
     */
    private fun groupStep(
        template: String,
        name: String,
        plural: Boolean,
        member: (EffectTarget) -> Effect,
    ): Phrase<CardScript> {
        fun scriptFor(filter: GameObjectFilter) = CardScript(
            spellEffect = Effects.ForEachInGroup(GroupFilter(filter), member(EffectTarget.Self)),
        )
        return phrase(template, name = name) {
            slot("filter", if (plural) Filters.plural else Filters.filter)
            build { scriptFor(it.value("filter")) }
            match { script ->
                val filter = iteratedGroup(script.spellEffect) ?: return@match null
                if (script != scriptFor(filter)) return@match null
                bind("filter" to filter)
            }
        }
    }

    /**
     * The mass effects whose per-member effect also carries a number or a keyword.
     *
     * Written as its own shape rather than as a parameter on [groupStep] because the extra slot
     * changes both halves of the inversion: `member` has to be reconstructed from a value read back
     * out of the iterated effect, which [groupStep]'s fixed `member` never needs.
     */
    private fun <V> parameterizedGroupStep(
        template: String,
        name: String,
        parameter: Phrase<V>,
        plural: Boolean,
        member: (V, EffectTarget) -> Effect,
        read: (Effect) -> V?,
        canonicalForm: Boolean = true,
    ): Phrase<CardScript> {
        fun scriptFor(value: V, filter: GameObjectFilter) = CardScript(
            spellEffect = Effects.ForEachInGroup(GroupFilter(filter), member(value, EffectTarget.Self)),
        )
        val rule = phrase<CardScript>(template, name = name) {
            if (template.contains("{self}")) slot("self", Primitives.self)
            slot("filter", if (plural) Filters.plural else Filters.filter)
            slot("v", parameter)
            build { scriptFor(it.value("v"), it.value("filter")) }
            match { script ->
                val filter = iteratedGroup(script.spellEffect) ?: return@match null
                val value = read(iteratedBody(script.spellEffect) ?: return@match null) ?: return@match null
                if (script != scriptFor(value, filter)) return@match null
                bind("self" to Unit, "filter" to filter, "v" to value)
            }
            canonical = canonicalForm
        }
        return if (canonicalForm) rule else alternate(rule)
    }

    /**
     * "Soldier creatures get +1/+1 and gain first strike until end of turn." — Gempalm Avenger.
     *
     * [pumpAndGrantTarget]'s group-side twin, and one rule for the same reason: the second clause
     * has no subject of its own, and the model is two iterations over the *same* group rather than a
     * compound effect. That the group is written twice is the SDK's shape, not a redundancy this
     * rule invents, so the reconstruction compares both.
     */
    private fun groupPumpAndGrant(prefix: String, name: String, canonicalForm: Boolean): Phrase<CardScript> {
        fun scriptFor(modifiers: Pair<Int, Int>, keyword: Keyword, filter: GameObjectFilter) = CardScript(
            spellEffect = Effects.Composite(
                listOf(
                    Effects.ForEachInGroup(
                        GroupFilter(filter),
                        Effects.ModifyStats(modifiers.first, modifiers.second, EffectTarget.Self),
                    ),
                    Effects.ForEachInGroup(
                        GroupFilter(filter),
                        Effects.GrantKeyword(keyword, EffectTarget.Self),
                    ),
                )
            )
        )
        val rule = phrase<CardScript>("$prefix{filter} get {mod} and gain {kw} until end of turn", name = name) {
            slot("filter", Filters.plural)
            slot("mod", Primitives.statModifiers)
            slot("kw", Keywords.keyword)
            build { scriptFor(it.value("mod"), it.value("kw"), it.value("filter")) }
            match { script ->
                val effects = (script.spellEffect as? CompositeEffect)?.effects ?: return@match null
                val filter = iteratedGroup(effects.firstOrNull()) ?: return@match null
                val modifiers = fixedModifiers(iteratedBody(effects.firstOrNull())) ?: return@match null
                val keyword = grantedKeyword(iteratedBody(effects.getOrNull(1))) ?: return@match null
                if (script != scriptFor(modifiers, keyword, filter)) return@match null
                bind("filter" to filter, "mod" to modifiers, "kw" to keyword)
            }
            canonical = canonicalForm
        }
        return if (canonicalForm) rule else alternate(rule)
    }

    /**
     * The same shape over a group that **excludes the source** — "tap all *other* creatures."
     *
     * `excludeSelf` is a field on the `GroupFilter` rather than on the base filter, which is exactly
     * why it is a separate rule and not a [Filters] layer: "other" is a fact about the iteration's
     * relationship to the ability's source, not about what a permanent is.
     */
    private fun otherGroupStep(
        template: String,
        name: String,
        member: (EffectTarget) -> Effect,
    ): Phrase<CardScript> {
        fun scriptFor(filter: GameObjectFilter) = CardScript(
            spellEffect = Effects.ForEachInGroup(
                GroupFilter(filter, excludeSelf = true),
                member(EffectTarget.Self),
            ),
        )
        return phrase(template, name = name) {
            slot("filter", Filters.plural)
            build { scriptFor(it.value("filter")) }
            match { script ->
                val filter = iteratedGroup(script.spellEffect) ?: return@match null
                if (script != scriptFor(filter)) return@match null
                bind("filter" to filter)
            }
        }
    }

    /**
     * "Destroy all creatures. They can't be regenerated." — Wrath of God.
     *
     * Two printed sentences and one model: `noRegenerate` is a field on the *same* iteration, not a
     * second effect, so [sequence] has nothing to split. The rule therefore spans both sentences,
     * which is what makes the plain "Destroy all creatures." rule above safe — a sweep that forbids
     * regeneration refuses to print as one that does not.
     */
    private val destroyAllNoRegenerate: Phrase<CardScript> = run {
        fun scriptFor(filter: GameObjectFilter) = CardScript(
            spellEffect = Effects.ForEachInGroup(
                GroupFilter(filter),
                Effects.Destroy(EffectTarget.Self),
                noRegenerate = true,
            ),
        )
        phrase("destroy all {filter}. they can't be regenerated", name = "destroy all without regeneration") {
            slot("filter", Filters.plural)
            build { scriptFor(it.value("filter")) }
            match { script ->
                val filter = iteratedGroup(script.spellEffect) ?: return@match null
                if (script != scriptFor(filter)) return@match null
                bind("filter" to filter)
            }
        }
    }

    private val groupSteps: List<Phrase<CardScript>> = listOf(
        groupStep("destroy all {filter}", "destroy all", plural = true) { Effects.Destroy(it) },
        groupStep("exile all {filter}", "exile all", plural = true) { Effects.Exile(it) },
        groupStep("tap all {filter}", "tap all", plural = true) { Effects.Tap(it) },
        groupStep("untap all {filter}", "untap all", plural = true) { Effects.Untap(it) },
        otherGroupStep("tap all other {filter}", "tap all other") { Effects.Tap(it) },
        otherGroupStep("untap all other {filter}", "untap all other") { Effects.Untap(it) },
        destroyAllNoRegenerate,
        parameterizedGroupStep(
            "{filter} get {v} until end of turn", "a group gets",
            parameter = Primitives.statModifiers, plural = true,
            member = { (power, toughness), target -> Effects.ModifyStats(power, toughness, target) },
            read = ::fixedModifiers,
        ),
        parameterizedGroupStep(
            "{filter} gain {v} until end of turn", "a group gains a keyword",
            parameter = Keywords.keyword, plural = true,
            member = { keyword, target -> Effects.GrantKeyword(keyword, target) },
            read = ::grantedKeyword,
        ),
        parameterizedGroupStep(
            "{self} deals {v} damage to each {filter}", "deals damage to each",
            parameter = Primitives.cardinal, plural = false,
            member = { amount, target -> Effects.DealDamage(amount, target) },
            read = ::damageDealt,
        ),
        // "All creatures get -5/-5 until end of turn." — the same value with the word "all" in
        // front, which `GroupFilter` has no room for. The bare form is canonical because it is what
        // the modern lord and mass-pump templating prints; this parses and never prints, so those
        // cards come back as a variant. Same treatment [Statics.lordStatic] gives the static side.
        parameterizedGroupStep(
            "all {filter} get {v} until end of turn", "all of a group gets",
            parameter = Primitives.statModifiers, plural = true,
            member = { (power, toughness), target -> Effects.ModifyStats(power, toughness, target) },
            read = ::fixedModifiers,
            canonicalForm = false,
        ),
        parameterizedGroupStep(
            "all {filter} gain {v} until end of turn", "all of a group gains a keyword",
            parameter = Keywords.keyword, plural = true,
            member = { keyword, target -> Effects.GrantKeyword(keyword, target) },
            read = ::grantedKeyword,
            canonicalForm = false,
        ),
        groupPumpAndGrant("", "a group gets and gains", canonicalForm = true),
        groupPumpAndGrant("all ", "all of a group gets and gains", canonicalForm = false),
    )

    // ---------------------------------------------------------------------------------------
    // Damage whose amount is not a numeral
    // ---------------------------------------------------------------------------------------

    /**
     * "~ deals X damage to any target." — the X spells.
     *
     * `DynamicAmount.XValue` is a *constant* in the model and a literal in the text, so these go
     * through [amountStep] rather than [countedStep]: there is no number to read back, only a
     * reconstruction to compare against.
     */
    private val xDamageSteps: List<Phrase<CardScript>> = listOf(
        amountStep("{self} deals X damage to any target", "deals X damage to any target", DynamicAmount.XValue) {
            CardScript(
                spellEffect = Effects.DealDamage(it, Targets.bound()),
                targetRequirements = listOf(Targets.any()),
            )
        },
        amountStep(
            "{self} deals X damage to target player or planeswalker",
            "deals X damage to target player or planeswalker",
            DynamicAmount.XValue,
        ) {
            CardScript(
                spellEffect = Effects.DealDamage(it, Targets.bound()),
                targetRequirements = listOf(Targets.playerOrPlaneswalker()),
            )
        },
        amountStep(
            "{self} deals damage to target opponent or planeswalker equal to the sacrificed creature's power",
            "deals damage equal to the sacrificed creature's power",
            DynamicAmounts.sacrificedPower(),
        ) {
            CardScript(
                spellEffect = Effects.DealDamage(it, Targets.bound()),
                targetRequirements = listOf(Targets.opponentOrPlaneswalker()),
            )
        },
    )

    /**
     * "~ deals damage to target creature equal to the number of Mountains you control." — Spitting
     * Earth, and Fire Dragon's enters trigger with the same clause.
     *
     * Two filters in one sentence: the thing damaged and the thing counted. The count is the same
     * `AggregateBattlefield` [gainLifeForEach] builds, which is why "you control" is a literal here
     * — the player is the aggregate's field and the clause spells exactly one of them.
     */
    private val damageEqualToCount: Phrase<CardScript> = run {
        fun scriptFor(counted: GameObjectFilter, filter: GameObjectFilter) = CardScript(
            spellEffect = Effects.DealDamage(
                DynamicAmount.AggregateBattlefield(Player.You, counted),
                Targets.bound(),
            ),
            targetRequirements = listOf(Targets.permanent(filter)),
        )
        phrase(
            "{self} deals damage to target {filter} equal to the number of {counted} you control",
            name = "deals damage equal to a battlefield count",
        ) {
            slot("self", Primitives.self)
            slot("filter", Filters.filter)
            slot("counted", Filters.plural)
            build { scriptFor(it.value("counted"), it.value("filter")) }
            match { script ->
                val amount = (script.spellEffect as? DealDamageEffect)?.amount
                    as? DynamicAmount.AggregateBattlefield ?: return@match null
                val requirement = script.targetRequirements.singleOrNull() ?: return@match null
                val filter = Targets.permanentFilter(requirement) ?: return@match null
                if (script != scriptFor(amount.filter, filter)) return@match null
                bind("self" to Unit, "filter" to filter, "counted" to amount.filter)
            }
        }
    }

    /**
     * "~ deals 1 damage to each creature and each player." — the symmetric sweeps.
     *
     * One printed sentence, two effects: the board half is the ordinary [groupStep] iteration and
     * the player half is a single damage effect aimed at `Player.Each`. It is one rule rather than a
     * [sequence] because the sentence is one sentence; a card printing the two halves separately
     * denotes the identical model and comes back as a variant.
     *
     * **The player half has two SDK spellings and this emits one.** `DealDamage(n, PlayerRef(Each))`
     * and `ForEachPlayerEffect(Each, [DealDamage(n, Controller)])` are equivalent for a fixed amount;
     * the first is what the grammar prints, because per-player controller rebinding is machinery this
     * sentence does not need. Cards written the other way decline and are a reported inconsistency,
     * not an approximation.
     */
    private fun damageToEachAndEachPlayer(
        template: String,
        name: String,
        amount: Phrase<Int>?,
        fixed: DynamicAmount?,
    ): Phrase<CardScript> {
        fun scriptFor(value: DynamicAmount, filter: GameObjectFilter) = CardScript(
            spellEffect = Effects.Composite(
                listOf(
                    Effects.ForEachInGroup(GroupFilter(filter), Effects.DealDamage(value, EffectTarget.Self)),
                    Effects.DealDamage(value, EffectTarget.PlayerRef(Player.Each)),
                )
            )
        )
        return phrase(template, name = name) {
            slot("self", Primitives.self)
            slot("filter", Filters.filter)
            if (amount != null) slot("n", amount)
            build { bindings ->
                val value = fixed ?: DynamicAmount.Fixed(bindings.int("n"))
                scriptFor(value, bindings.value("filter"))
            }
            match { script ->
                val effects = (script.spellEffect as? CompositeEffect)?.effects ?: return@match null
                val filter = iteratedGroup(effects.firstOrNull()) ?: return@match null
                val value = (effects.getOrNull(1) as? DealDamageEffect)?.amount ?: return@match null
                if (fixed != null && value != fixed) return@match null
                val number = if (fixed != null) null else value.fixed() ?: return@match null
                if (script != scriptFor(value, filter)) return@match null
                bind("self" to Unit, "filter" to filter, "n" to number)
            }
        }
    }

    // ---------------------------------------------------------------------------------------
    // Counting the battlefield — "You gain 2 life for each Mountain target opponent controls."
    // ---------------------------------------------------------------------------------------

    /** "Draw a card for each tapped creature target opponent controls." — Theft of Dreams. */
    private val drawForEach: Phrase<CardScript> = run {
        fun scriptFor(counted: GameObjectFilter) = CardScript(
            spellEffect = Effects.DrawCards(DynamicAmount.AggregateBattlefield(Player.TargetOpponent, counted)),
            targetRequirements = listOf(Targets.opponent()),
        )
        phrase("draw a card for each {filter} target opponent controls", name = "draw for each") {
            slot("filter", Filters.filter)
            build { scriptFor(it.value("filter")) }
            match { script ->
                val amount = (script.spellEffect as? DrawCardsEffect)?.count
                    as? DynamicAmount.AggregateBattlefield ?: return@match null
                if (script != scriptFor(amount.filter)) return@match null
                bind("filter" to amount.filter)
            }
        }
    }

    /**
     * "You gain N life for each …" — the first rules whose amount is a [DynamicAmount] rather than
     * a numeral.
     *
     * Two shapes over disjoint counts, for exactly the reason the draw rules are two: the SDK writes
     * "1 life for each X" as the bare count and "2 life for each X" as `Multiply(count, 2)`, so one
     * is not a special case of the other in the model either. Refusing 1 in the multiplying rule is
     * what keeps one printed form per model.
     *
     * The *whose battlefield* half is a `Player` inside the aggregate and, when it is a targeted
     * one, a `TargetRequirement` beside it — two places for one printed clause, which is why the
     * surface and both halves are passed together rather than derived.
     */
    private fun gainLifeForEach(
        surface: String,
        player: Player,
        requirements: List<TargetRequirement>,
    ): List<Phrase<CardScript>> {
        fun scriptFor(amount: DynamicAmount) = CardScript(
            spellEffect = Effects.GainLife(amount),
            targetRequirements = requirements,
        )

        fun count(filter: GameObjectFilter): DynamicAmount = DynamicAmount.AggregateBattlefield(player, filter)

        /** The aggregate this surface spells, or null when the value counts someone else's board. */
        fun aggregate(amount: DynamicAmount?): DynamicAmount.AggregateBattlefield? =
            (amount as? DynamicAmount.AggregateBattlefield)
                ?.takeIf { it == count(it.filter) }

        val one = phrase("you gain 1 life for each {filter} $surface", name = "gain one life for each") {
            slot("filter", Filters.filter)
            build { scriptFor(count(it.value("filter"))) }
            match { script ->
                val total = aggregate((script.spellEffect as? GainLifeEffect)?.amount) ?: return@match null
                if (script != scriptFor(total)) return@match null
                bind("filter" to total.filter)
            }
        }
        val many = phrase("you gain {n} life for each {filter} $surface", name = "gain life for each") {
            slot("n", Primitives.cardinal)
            slot("filter", Filters.filter)
            build { bindings ->
                val multiplier = bindings.int("n")
                if (multiplier < 2) return@build null
                scriptFor(DynamicAmount.Multiply(count(bindings.value("filter")), multiplier))
            }
            match { script ->
                val product = (script.spellEffect as? GainLifeEffect)?.amount as? DynamicAmount.Multiply
                    ?: return@match null
                val total = aggregate(product.amount) ?: return@match null
                if (product.multiplier < 2) return@match null
                if (script != scriptFor(product)) return@match null
                bind("n" to product.multiplier, "filter" to total.filter)
            }
        }
        return listOf(one, many)
    }

    // ---------------------------------------------------------------------------------------
    // The sentence, the sequence, and the line
    // ---------------------------------------------------------------------------------------

    /**
     * Every clause rule, as one alternation.
     *
     * [Mana.addClause] is a member declared elsewhere: producing mana is a spell effect in its own
     * right (Dark Ritual) *and* the effect clause of nearly every activated ability, so its two
     * halves — the sentence and the choice form that denotes several abilities — are kept together
     * in [Mana] rather than split across two files by which slot each one reaches. [Library],
     * [Hand], [SelfSteps] and [Combat] are the same: one file per topic, all of them rows here.
     */
    /**
     * "You gain 3 life for each creature attacking you." — Blessed Reversal.
     *
     * Not a surface of [gainLifeForEach], because "attacking you" is *two* fields in the model — the
     * opponents' battlefield and the attacking state — for one printed phrase, and a rule that
     * slotted the noun would have to put half of the phrase in the slot and half in the surface.
     * Spelled once, with the noun fixed.
     */
    private val gainLifePerAttacker: Phrase<CardScript> = run {
        fun scriptFor(multiplier: Int) = CardScript(
            spellEffect = Effects.GainLife(
                DynamicAmount.Multiply(
                    DynamicAmount.AggregateBattlefield(
                        Player.EachOpponent,
                        GameObjectFilter.Creature.attacking(),
                    ),
                    multiplier,
                )
            )
        )
        phrase("you gain {n} life for each creature attacking you", name = "gain life per attacker") {
            slot("n", Primitives.cardinal)
            build { bindings -> bindings.int("n").takeIf { it >= 2 }?.let(::scriptFor) }
            match { script ->
                val product = (script.spellEffect as? GainLifeEffect)?.amount as? DynamicAmount.Multiply
                    ?: return@match null
                if (product.multiplier < 2 || script != scriptFor(product.multiplier)) return@match null
                bind("n" to product.multiplier)
            }
        }
    }

    private val atomicClauses: List<Phrase<CardScript>> =
        listOf(drawOne, drawMany, targetPlayerDrawsOne, targetPlayerDrawsMany) +
            countedSteps +
            xDamageSteps +
            damageToTargetPermanent +
            damageEqualToCount +
            damageToEachAndEachPlayer(
                "{self} deals {n} damage to each {filter} and each player",
                "deals damage to each permanent and each player",
                amount = Primitives.cardinal,
                fixed = null,
            ) +
            damageToEachAndEachPlayer(
                "{self} deals X damage to each {filter} and each player",
                "deals X damage to each permanent and each player",
                amount = null,
                fixed = DynamicAmount.XValue,
            ) +
            pumpTargetPermanent +
            grantToTargetPermanent +
            pumpAndGrantTarget +
            permanentSteps +
            groupSteps +
            drawForEach +
            gainLifePerAttacker +
            gainLifeForEach("on the battlefield", Player.Each, emptyList()) +
            gainLifeForEach("target opponent controls", Player.TargetOpponent, listOf(Targets.opponent())) +
            turnSteps +
            sentenceClauses +
            exchangeControl +
            Stack.clauses +
            Mana.addClause +
            Mana.addClauses +
            Library.clauses +
            Hand.clauses +
            Combat.clauses +
            Graveyard.clauses +
            Amounts.clauses +
            Morph.clauses +
            CreatureTypes.clauses +
            Tokens.clauses +
            SelfSteps.clauses +
            SelfSteps.anaphoric

    /**
     * One clause, plus the joined form of two.
     *
     * "~ deals 4 damage to any target and you gain 4 life." and "~ deals 4 damage to any target. You
     * gain 4 life." denote the identical `CompositeEffect`: the model has no room for the
     * conjunction, so exactly one of the two is canonical and the other parses without printing.
     * The sequence wins because it is what the corpus overwhelmingly prints and because it composes
     * to any length; a card printing the join comes back as a
     * [com.wingedsheep.assay.gate.LineVerdict.VARIANT], which says the reading was right and only
     * the spelling moved.
     */
    /**
     * The atoms alone, for the rules that wrap or join clauses without being one.
     *
     * Declared before everything built from it — object initializers run in declaration order, and a
     * `val` reaching a later one reads a null out of a half-initialized object.
     */
    private val atom: Phrase<CardScript> = oneOf("a spell effect", atomicClauses)

    /**
     * "You may draw a card." — the controller chooses whether the clause happens.
     *
     * Wrapping rather than a vocabulary of its own, so every clause the grammar can read is
     * optional-able for free. Note that a *triggered* ability spells the same English with its own
     * `optional` flag instead; [Triggers] lowers this wrapper into that field, which is what keeps
     * one printed form for the two SDK spellings.
     */
    private val mayClause: Phrase<CardScript> = phrase("you may {inner}", name = "you may") {
        slot("inner", atom)
        build { bindings -> wrap(bindings.value("inner")) { MayEffect(it) } }
        match { script ->
            val gated = script.spellEffect as? GatedEffect ?: return@match null
            if (gated.gate !is Gate.MayDecide) return@match null
            val inner = CardScript(spellEffect = gated.then, targetRequirements = script.targetRequirements)
            if (wrap(inner) { MayEffect(it) } != script) return@match null
            bind("inner" to inner)
        }
    }

    /**
     * "You may pay {B}{B}{B}. If you do, return it to your hand." — Ghastly Remains, Skirk Drill
     * Sergeant, and Hollow Specter's `{X}` sibling.
     *
     * Two printed sentences and one wrapper, because the second is the *consequence* of the first:
     * `Gate.MayPay` holds both the cost and what follows, so there is nothing for [sequenceClause] to
     * split. A wrapper for the same reason [mayClause] is one — every clause the grammar can read
     * becomes payable-for at no cost.
     *
     * The `{X}` form is a separate rule rather than a mana cost that happens to be `{X}`: the model
     * is a different gate, because the player chooses the number rather than paying a printed one.
     * The printed symbol is the same either way, so the mana rule **refuses `{X}` outright** —
     * without that, Decree of Justice's cycling trigger has two readings with two different models,
     * which is the hard ambiguity the design says never to resolve by ordering an alternation.
     */
    private val payX: ManaCost = ManaCost.parse("{X}")

    private val mayPayClauses: List<Phrase<CardScript>> = listOf(
        phrase("you may pay {cost}. if you do, {inner}", name = "you may pay a cost") {
            slot("cost", Primitives.manaCost)
            slot("inner", atom)
            build { bindings ->
                val cost = bindings.value<ManaCost>("cost")
                if (cost == payX) return@build null
                wrap(bindings.value("inner")) { MayPayManaEffect(cost, it) }
            }
            match { script ->
                val gated = script.spellEffect as? GatedEffect ?: return@match null
                val gate = gated.gate as? Gate.MayPay ?: return@match null
                val cost = (gate.cost as? PayManaCostEffect)?.cost ?: return@match null
                if (cost == payX) return@match null
                val inner = CardScript(spellEffect = gated.then, targetRequirements = script.targetRequirements)
                if (wrap(inner) { MayPayManaEffect(cost, it) } != script) return@match null
                bind("cost" to cost, "inner" to inner)
            }
        },
        phrase("you may pay {X}. if you do, {inner}", name = "you may pay X") {
            slot("inner", atom)
            build { bindings -> wrap(bindings.value("inner")) { MayPayXForEffect(it) } }
            match { script ->
                val gated = script.spellEffect as? GatedEffect ?: return@match null
                if (gated.gate !is Gate.MayPayX) return@match null
                val inner = CardScript(spellEffect = gated.then, targetRequirements = script.targetRequirements)
                if (wrap(inner) { MayPayXForEffect(it) } != script) return@match null
                bind("inner" to inner)
            }
        },
    )

    /**
     * "If an opponent controls more lands than you, search your library for …" — Gift of Estates.
     *
     * The SDK lowers a spell's `condition` into a `ConditionalEffect` wrapping the whole effect, so
     * this is a wrapper for the same reason [mayClause] is one, and the condition is the slot. The
     * condition vocabulary is [Conditions]; it has two members, which is what a condition family
     * looks like the first time cards need one.
     */
    private val conditionalClause: Phrase<CardScript> =
        phrase("if {cond}, {inner}", name = "a conditional clause") {
            slot("cond", Conditions.condition)
            slot("inner", atom)
            build { bindings ->
                val condition = bindings.value<Condition>("cond")
                wrap(bindings.value("inner")) { ConditionalEffect(condition, it) }
            }
            match { script ->
                val gated = script.spellEffect as? GatedEffect ?: return@match null
                val gate = gated.gate as? Gate.WhenCondition ?: return@match null
                val inner = CardScript(spellEffect = gated.then, targetRequirements = script.targetRequirements)
                if (wrap(inner) { ConditionalEffect(gate.condition, it) } != script) return@match null
                bind("cond" to gate.condition, "inner" to inner)
            }
        }

    /** Re-wrap a clause's effect, keeping the targets it declared. Shared by the two wrappers. */
    private fun wrap(inner: CardScript, wrapper: (Effect) -> Effect): CardScript? {
        val effect = inner.spellEffect ?: return null
        if (inner != CardScript(spellEffect = effect, targetRequirements = inner.targetRequirements)) return null
        return CardScript(spellEffect = wrapper(effect), targetRequirements = inner.targetRequirements)
    }

    /** One self-contained clause: an atom, or an atom under a wrapper. */
    private val simpleClause: Phrase<CardScript> =
        oneOf("a spell effect", atomicClauses + mayClause + conditionalClause + mayPayClauses)

    /**
     * A clause that can only be a *later* one: it refers back to something an earlier clause
     * introduced, so it declares nothing of its own.
     */
    private val laterClause: Phrase<CardScript> = oneOf(
        "a spell effect",
        // Everything except the source-anaphora: once a clause has introduced a target, "it" means
        // that target, and [Continuations] owns the pronoun from here on. See [SelfSteps.anaphoric].
        (atomicClauses - SelfSteps.anaphoric.toSet()) + mayClause + conditionalClause + mayPayClauses +
            Continuations.all,
    )

    /**
     * What joins one clause to the next inside a line — a full stop, "and", or ", then".
     *
     * All three denote the same thing, because a `CompositeEffect` has no room for the conjunction:
     * the model says *these effects, in this order* and nothing about the word between them. So the
     * full stop is canonical and the other two are [alternate]s, and a card printing a join comes
     * back as a [com.wingedsheep.assay.gate.LineVerdict.VARIANT] — the reading was right, only the
     * spelling moved, which is worth strictly more than the decline it replaces.
     *
     * The separator belongs to the *tail* rather than to the run, which is what lets one line mix
     * them: "Scry 2, then draw two cards. You lose 2 life." is three clauses joined two different
     * ways, and it folds to the same flat composite the all-full-stops spelling does. A run with one
     * separator could not read that line at all, and a join rule that was itself a clause would have
     * folded it into a *nested* composite — a model no card carries and nothing could print.
     */
    private fun tail(separator: String, canonicalJoin: Boolean): Phrase<CardScript> {
        val rule = phrase<CardScript>("$separator{clause}", name = "a later clause") {
            slot("clause", laterClause)
            build { it.value("clause") }
            match { bind("clause" to it) }
            canonical = canonicalJoin
        }
        return if (canonicalJoin) rule else alternate(rule)
    }

    private val tails: Phrase<CardScript> = oneOf(
        "a later clause",
        tail(". ", canonicalJoin = true),
        tail(", then ", canonicalJoin = false),
        tail(" and ", canonicalJoin = false),
    )

    /**
     * "Target creature gets +1/+3 until end of turn. Untap that creature." — two or more clauses on
     * one printed line, which the SDK models as one `CompositeEffect`.
     *
     * **A target is declared at its first mention, which is not always the first clause.** English
     * introduces a referent before it refers back to one, so the requirement belongs to the clause
     * that names it and every later one is either self-contained or a [Continuations] clause reading
     * the slot that clause declared. Fleshformer is what a line looks like when the introducing
     * clause is *second*: "~ gets +2/+2 and gains fear until end of turn. Target creature gets -2/-2
     * until end of turn."
     *
     * `match` therefore looks for the owning clause rather than assuming index 0 — but it decides by
     * *printability*, not by preference: a clause that needs the requirement cannot print without
     * it, and one that does not cannot print with it, because every rule here reconstructs the whole
     * script and compares. At most one position can satisfy both, so the split stays deterministic
     * and a model no position can print declines rather than being guessed at.
     *
     * The run's separator is the empty string because each tail carries its own; see [tail].
     */
    private val sequenceClause: Phrase<CardScript> = phrase("{first}{rest}", name = "several clauses") {
        slot("first", simpleClause)
        slot("rest", separated("later clauses", tails, separator = "", min = 1))
        build { merge(listOf(it.value<CardScript>("first")) + it.value<List<CardScript>>("rest")) }
        match { script ->
            val composite = script.spellEffect as? CompositeEffect ?: return@match null
            if (composite.effects.size < 2) return@match null
            if (composite != CompositeEffect(composite.effects)) return@match null
            val owner = composite.effects.indices.firstOrNull { index ->
                val candidate = clauseParts(composite.effects, script.targetRequirements, index)
                merge(candidate) == script && printable(candidate)
            } ?: return@match null
            val parts = clauseParts(composite.effects, script.targetRequirements, owner)
            bind("first" to parts.first(), "rest" to parts.drop(1))
        }
    }

    /** The line's clauses, with the whole line's requirements attached to clause [owner]. */
    private fun clauseParts(
        effects: List<Effect>,
        requirements: List<TargetRequirement>,
        owner: Int,
    ): List<CardScript> = effects.mapIndexed { index, effect ->
        CardScript(
            spellEffect = effect,
            targetRequirements = if (index == owner) requirements else emptyList(),
        )
    }

    /** True when every clause of a split can be printed from the position it sits in. */
    private fun printable(parts: List<CardScript>): Boolean =
        simpleClause.unparse(parts.first()) != null && parts.drop(1).all { laterClause.unparse(it) != null }

    /** Everything one clause position can hold. */
    private val clause: Phrase<CardScript> = oneOf("a spell effect", simpleClause, sequenceClause)

    /** One clause and the stop that ends it — what a whole effect line is. */
    private val sentence: Phrase<CardScript> = phrase("{clause}.", name = "a sentence") {
        slot("clause", clause)
        build { it.value("clause") }
        match { bind("clause" to it) }
    }

    /**
     * Fold clause scripts into the one script the line denotes, or null when they cannot be one.
     *
     * A clause may contribute a spell effect and the targets it declared and nothing else; anything
     * more is content this fold would silently drop, so it refuses instead.
     *
     * ### Two clauses that each declare a target refuse to fold
     *
     * [Targets.SLOT] is a single fixed name, which is enough while every rule takes at most one
     * target — and stops being enough exactly here. "Destroy target land. ~ deals 13 damage to
     * target creature." would fold into a script with *two* requirements both called `target` and
     * two effects both reading that name, which is not the card: it is a model in which the second
     * slot has no way to be referred to. Refusing is the fail-closed answer, and the gap it names is
     * a slot-name **generator** in [Targets] rather than anything about this fold. Until that
     * exists, those lines decline and are counted.
     */
    private fun merge(parts: List<CardScript>): CardScript? {
        if (parts.count { it.targetRequirements.isNotEmpty() } > 1) return null
        val effects = parts.map { part ->
            val effect = part.spellEffect ?: return null
            if (part != CardScript(spellEffect = effect, targetRequirements = part.targetRequirements)) return null
            effect
        }
        return CardScript(
            spellEffect = if (effects.size == 1) effects.single() else Effects.Composite(effects),
            targetRequirements = parts.flatMap { it.targetRequirements },
        )
    }

    /**
     * What a spell's whole effect text denotes: one sentence, or a clause that ends itself.
     *
     * [sentence] spells the full stop, which is right for every clause whose text ends on one. A
     * clause ending *inside a quotation* does not — "…gains "This creature can't attack …."" closes
     * on a quote mark — so those are offered beside it rather than inside it, and the two are
     * disjoint by their last character.
     */
    val step: Phrase<CardScript> = oneOf("a spell effect line", listOf(sentence) + Combat.selfTerminatingClauses)

    // ---------------------------------------------------------------------------------------
    // Model helpers — the `match` side, kept out of the rules so like rules read alike
    // ---------------------------------------------------------------------------------------

    private fun targetPlayerDraws(count: Int) = CardScript(
        spellEffect = Effects.DrawCards(count, Targets.bound()),
        targetRequirements = listOf(Targets.player()),
    )

    /** The count on a bare "the caster draws" script, or null when the script is anything else. */
    private fun drawnByController(script: CardScript): Int? =
        drawCount(script, requireTarget = false)?.takeIf { script.targetRequirements.isEmpty() }

    /** …and on a "target player draws" script, which must carry the matching requirement. */
    private fun drawnByTarget(script: CardScript): Int? =
        drawCount(script, requireTarget = true)
            ?.takeIf { script.targetRequirements == listOf(Targets.player()) }

    /**
     * The fixed amounts the counted verbs read back.
     *
     * Each recovers only the *number*; nothing here checks the target or the rest of the script,
     * because [countedStep]'s equality against its own `script(n)` already does, exhaustively. A
     * dynamic amount ("equal to the number of…") has no numeral to print, so it declines here.
     */
    internal fun lifeGained(effect: Effect): Int? = (effect as? GainLifeEffect)?.amount?.fixed()

    internal fun lifeLost(effect: Effect): Int? = (effect as? LoseLifeEffect)?.amount?.fixed()

    internal fun damageDealt(effect: Effect): Int? = (effect as? DealDamageEffect)?.amount?.fixed()

    /** The two fixed bonuses a `ModifyStats` effect carries, or null for a dynamic one. */
    internal fun fixedModifiers(effect: Effect?): Pair<Int, Int>? {
        val stats = effect as? ModifyStatsEffect ?: return null
        val power = stats.powerModifier.fixed() ?: return null
        val toughness = stats.toughnessModifier.fixed() ?: return null
        return power to toughness
    }

    /**
     * The [Keyword] a grant effect names, or null when it names a synthesized marker instead.
     *
     * `GrantKeywordEffect` holds a `String`, which is wider than the enum: the SDK also uses the
     * field for markers like `PROTECTION_FROM_BLACK` that no constant names. Reading it back has to
     * find the constant rather than assume one.
     */
    internal fun grantedKeyword(effect: Effect?): Keyword? {
        val grant = effect as? com.wingedsheep.sdk.scripting.effects.GrantKeywordEffect ?: return null
        return Keyword.entries.firstOrNull { it.name == grant.keyword }
    }

    /** The group a mass effect iterates, or null when the effect is not a plain battlefield sweep. */
    private fun iteratedGroup(effect: Effect?): GameObjectFilter? {
        val forEach = effect as? com.wingedsheep.sdk.scripting.effects.ForEachEffect ?: return null
        val space = forEach.space as? com.wingedsheep.sdk.scripting.effects.IterationSpace.Group ?: return null
        return space.filter.baseFilter
    }

    private fun iteratedBody(effect: Effect?): Effect? =
        (effect as? com.wingedsheep.sdk.scripting.effects.ForEachEffect)?.body

    internal fun DynamicAmount.fixed(): Int? = (this as? DynamicAmount.Fixed)?.amount

    private fun drawCount(script: CardScript, requireTarget: Boolean): Int? {
        val effect = script.spellEffect as? DrawCardsEffect ?: return null
        if (script.copy(spellEffect = null, targetRequirements = emptyList()) != CardScript.EMPTY) return null
        val drawer = if (requireTarget) Targets.isBound(effect.target) else effect.target == EffectTarget.Controller
        if (!drawer) return null
        return (effect.count as? DynamicAmount.Fixed)?.amount
    }
}
