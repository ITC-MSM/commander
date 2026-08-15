package com.wingedsheep.assay.grammar

import com.wingedsheep.assay.normalize.Normalizer
import com.wingedsheep.assay.syntax.Phrase
import com.wingedsheep.assay.syntax.alternate
import com.wingedsheep.assay.syntax.bind
import com.wingedsheep.assay.syntax.oneOf
import com.wingedsheep.assay.syntax.phrase
import com.wingedsheep.sdk.scripting.events.DamageType
import com.wingedsheep.sdk.model.CardScript
import com.wingedsheep.sdk.scripting.AbilityId
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.scripting.EventPattern
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect
import com.wingedsheep.sdk.scripting.effects.Gate
import com.wingedsheep.sdk.scripting.effects.GatedEffect
import com.wingedsheep.sdk.scripting.effects.MayEffect
import com.wingedsheep.sdk.scripting.TriggerSpec
import com.wingedsheep.sdk.scripting.conditions.Condition
import com.wingedsheep.sdk.scripting.TriggeredAbility
import com.wingedsheep.sdk.dsl.Triggers as SdkTriggers

/**
 * The trigger prefix — "When ~ enters, draw a card." — and with it the first rules that reach a
 * `CardScript` slot other than the spell effect.
 *
 * ### The prefix is a rule; the clause after it is [Steps]
 *
 * A trigger is a sentence made of a *when* clause and an *effect* clause, and the effect clause is
 * the same English a spell prints: "draw a card.", "destroy target creature." So the rules here slot
 * [Steps.step] whole and lift its `CardScript` onto the ability — the effect becomes the trigger's
 * effect, and a target it declared becomes the trigger's `targetRequirement`, which is where a
 * `TriggeredAbility` keeps it. That lift is the entire relationship between the two files, and it is
 * why adding a step rule makes every trigger rule richer for free.
 *
 * ### Self-reference is normalization's problem, not the grammar's
 *
 * The rules spell the source as `~`. Both printed spellings — the card's own name on older cards,
 * "this creature" on modern ones — are abstracted to that token by
 * [com.wingedsheep.assay.normalize.Normalizer], which restores the exact printed word afterwards.
 * The grammar therefore never has to know which noun a card's type line makes it print, and neither
 * spelling is privileged over the other.
 *
 * ### `AbilityId` is arbitrary, in exactly the way a target slot's name is
 *
 * `CardDefinition`s carry generated ids — Kavu Climber's golden says `"ability_1"` — that no printed
 * text determines. The grammar mints one fixed id and the differential normalizes both sides by
 * position, the same treatment target slot names get. A rule that tried to reproduce the id would be
 * reading a counter, not a card.
 */
object Triggers {

    /**
     * The id every parsed trigger carries.
     *
     * One constant rather than a generator: the printed text does not determine it, so any value is
     * as right as any other, and a fixed one keeps two parses of the same line equal. The
     * differential renames it by position before comparing.
     */
    private val ID = AbilityId("trigger")

    /**
     * "when ~ enters, {effect}" — a whole triggered ability.
     *
     * The `match` half is fail-closed the same way the step rules are: it reconstructs what `build`
     * would have produced from the ability's own effect and target and compares the whole thing, so
     * an ability carrying anything the phrase does not spell — an intervening-if condition, an
     * `elseEffect`, a graveyard `activeZones`, "you may", a once-per-turn cap — refuses to print
     * rather than printing a sentence that quietly drops it. Only the id is exempt, because the id
     * is not in the text.
     */
    private fun triggerRule(surface: String, spec: TriggerSpec): Phrase<TriggeredAbility> {
        return phrase("$surface, {effect}", name = surface) {
            slot("effect", Steps.step)
            build { abilityFor(spec, it.value("effect")) }
            match { ability ->
                val script = scriptFor(ability)
                if (abilityFor(spec, script)?.copy(id = ability.id) != ability) return@match null
                bind("effect" to script)
            }
        }
    }

    /**
     * Build the ability a trigger's effect clause denotes — **including the lowering of "you may".**
     *
     * A triggered ability spells the controller's choice with its own `optional` flag, which is what
     * every hand-written card sets; a spell spells the identical English as a `MayEffect` around the
     * effect, because a spell has no such flag. Both are real SDK spellings of one sentence, so
     * reading "you may …" here has to *lower* one into the other rather than register a second rule:
     * a rule per spelling would be two readings of one text, which is the ambiguity the design says
     * never to resolve by picking one.
     *
     * The lowering runs in both directions — [scriptFor] wraps `optional` back into a `MayEffect`
     * before the comparison — so the round trip is over the same value in both halves.
     */
    private fun abilityFor(spec: TriggerSpec, script: CardScript): TriggeredAbility? {
        val effect = script.spellEffect ?: return null
        val gated = effect as? GatedEffect
        val optional = gated != null && gated.gate is Gate.MayDecide &&
            gated == MayEffect(gated.then)
        return TriggeredAbility(
            id = ID,
            trigger = spec.event,
            binding = spec.binding,
            // CR 603.4's intervening-if is **lifted, not duplicated**. A condition printed between
            // the event and the effect belongs in `triggerCondition`, which is the SDK's dedicated
            // slot for it, and the clause's own `Gate.WhenCondition` is then the same fact written a
            // second time — the thing this module's rule "a value the SDK carries twice is derived,
            // not spelled" exists to stop. So the gate is stripped from the effect exactly when it
            // is lifted, which is also what every hand-written card does: 478 of them set
            // `triggerCondition` and none pairs it with a gate. The differential reported Beastbond
            // Outcaster, Donatello and Phage the Untouchable while the gate was kept.
            //
            // That the engine checks `triggerCondition` only at detection time and not again on
            // resolution is a **rules gap in the engine**, not something a parser may paper over by
            // emitting a second condition — a card written that way would carry a condition its
            // 508 siblings don't, and the fix belongs where the rule is enforced. It is not a
            // one-liner there either: the field is overloaded. 340 cards use it for an
            // intervening-"if" (two checks), 47 for a "while" clause — "Whenever this creature
            // attacks **while** you control a Dinosaur" is trigger-time only, and Burning Sun
            // Cavalry and Seasoned Warrenguard have scenario tests asserting exactly that — and
            // ~100 for other trigger-time restrictions. Rechecking all of them uniformly fails
            // those two tests, so the engine fix needs "if" and "while" separated first.
            effect = liftInterveningIf(if (optional) (effect as GatedEffect).then else effect),
            triggerCondition = interveningIf(effect),
            // A `TriggeredAbility` keeps its first requirement in a field of its own and the rest in
            // a list beside it, which is the shape a clause declaring two targets lands in —
            // Chromeshell Crab's exchange. The split is the SDK's; nothing in the text says it.
            targetRequirement = script.targetRequirements.firstOrNull(),
            additionalTargetRequirements = script.targetRequirements.drop(1),
            optional = optional,
        )
    }

    /**
     * The condition an intervening-if states, or null when the effect does not open with one.
     *
     * Only a *top-level* `Gate.WhenCondition` counts, and only where the gate is the whole of the
     * effect: "When ~ enters, if X, do Y." is an intervening-if, while a condition buried inside a
     * later clause of a sequence is an ordinary conditional that resolves once.
     */
    private fun interveningIf(effect: com.wingedsheep.sdk.scripting.effects.Effect): Condition? {
        val gated = effect as? GatedEffect ?: return null
        return (gated.gate as? Gate.WhenCondition)?.condition
    }

    /** The effect that is left once [interveningIf] has taken the condition out of it. */
    private fun liftInterveningIf(
        effect: com.wingedsheep.sdk.scripting.effects.Effect,
    ): com.wingedsheep.sdk.scripting.effects.Effect =
        if (interveningIf(effect) != null) (effect as GatedEffect).then else effect

    /**
     * The inverse of the lowering: the clause script an ability's effect and targets denote.
     *
     * The two wrappers go back on in the order [abilityFor] took them off — the intervening-if
     * inside, "you may" outside — so the round trip is over the same value in both halves.
     */
    private fun scriptFor(ability: TriggeredAbility): CardScript {
        val conditioned = ability.triggerCondition
            ?.let { ConditionalEffect(condition = it, effect = ability.effect) }
            ?: ability.effect
        return CardScript(
            spellEffect = if (ability.optional) MayEffect(conditioned) else conditioned,
            targetRequirements = listOfNotNull(ability.targetRequirement) +
                ability.additionalTargetRequirements,
        )
    }

    /**
     * The trigger events with an unambiguous one-clause surface form.
     *
     * "When" versus "Whenever" is a property of the event rather than a choice: an event that
     * happens once to a permanent is templated "When", a repeatable one "Whenever". Baking the word
     * into each rule is what keeps one printed form per model.
     */
    /**
     * "At the beginning of your upkeep, …" — the step triggers, which are one family in the SDK
     * (`StepEvent(step, player)`) and one family here.
     *
     * They are the same rule shape as the event triggers with a different prefix, which is the whole
     * reason [triggerRule] was written as a function: a step trigger's effect clause is the same
     * English a spell prints, so it slots [Steps.step] and inherits every step rule for free.
     *
     * Declared before [rules], which uses it — object initializers run in declaration order, and a
     * `val` referencing a later one reads a null out of a half-initialized object.
     */
    /**
     * "At the beginning of your upkeep, if ~ is in your graveyard, …" — Ghastly Remains.
     *
     * The clause is not a condition at all: it says *where the ability works from*, which the model
     * carries as `activeZones` on the ability rather than as an intervening-if. Reading it as a
     * condition would round-trip and mean a different card — an upkeep trigger that fires from the
     * battlefield and then checks something. So it is a prefix variant rather than a
     * [Conditions] row, and the zone it names is the rule's parameter.
     */
    private fun zonedTriggerRule(surface: String, spec: TriggerSpec, zone: Zone): Phrase<TriggeredAbility> =
        phrase("$surface, {effect}", name = surface) {
            slot("effect", Steps.step)
            build { abilityFor(spec, it.value("effect"))?.copy(activeZones = setOf(zone)) }
            match { ability ->
                if (ability.activeZones != setOf(zone)) return@match null
                val script = scriptFor(ability)
                val rebuilt = abilityFor(spec, script)?.copy(id = ability.id, activeZones = setOf(zone))
                if (rebuilt != ability) return@match null
                bind("effect" to script)
            }
        }

    private val phaseRules: List<Phrase<TriggeredAbility>> = listOf(
        zonedTriggerRule(
            "at the beginning of your upkeep, if ${Normalizer.SELF} is in your graveyard",
            SdkTriggers.YourUpkeep,
            Zone.GRAVEYARD,
        ),
        triggerRule("at the beginning of your upkeep", SdkTriggers.YourUpkeep),
        triggerRule("at the beginning of your draw step", SdkTriggers.YourDrawStep),
        triggerRule("at the beginning of your end step", SdkTriggers.YourEndStep),
        triggerRule("at the beginning of your first main phase", SdkTriggers.FirstMainPhase),
        triggerRule("at the beginning of your second main phase", SdkTriggers.YourPostcombatMain),
        triggerRule("at the beginning of combat on your turn", SdkTriggers.BeginCombat),
        triggerRule("at the beginning of each upkeep", SdkTriggers.EachUpkeep),
        triggerRule("at the beginning of each end step", SdkTriggers.EachEndStep),
        triggerRule("at the beginning of each combat", SdkTriggers.EachCombat),
        triggerRule("at the beginning of each opponent's upkeep", SdkTriggers.EachOpponentUpkeep),
        // Wizards has templated the all-players steps both ways and both are current enough to
        // appear on cards in print: "each upkeep" (100 lines) beside "each player's upkeep" (83),
        // "each end step" (98) beside "each player's end step" (23). One model, two real English
        // spellings, so the more common one prints and the other parses — a VARIANT rather than a
        // decline, which says the reading was right and only the spelling moved.
        alternate(triggerRule("at the beginning of each player's upkeep", SdkTriggers.EachUpkeep)),
        alternate(triggerRule("at the beginning of each player's end step", SdkTriggers.EachEndStep)),
    )

    /**
     * The same shape over a trigger whose event names a **filter** — "Whenever a Beast enters, …",
     * "Whenever another creature enters, …".
     *
     * Written as a function of the spec *builder* rather than of a fixed spec, which is the whole
     * difference from [triggerRule]: the noun phrase is a slot, so the event has to be reconstructed
     * from whatever the filter turns out to be before the fail-closed comparison can run. Everything
     * else — the lift of [Steps.step], the "you may" lowering, the id exemption — is identical, and
     * a filtered rule therefore inherits every effect rule exactly as an unfiltered one does.
     *
     * The binding is part of the *surface*: "a Beast" is `ANY` and "another Beast" is `OTHER`, and
     * the word "another" is the only thing in the text that says so. Two rows, not an optional
     * literal, so the model decides which prints.
     *
     * [article] says which noun phrase the surface takes, and it is a property of the surface rather
     * than of the filter: "a Beast" carries its indefinite article and "another Beast" does not,
     * because "another" is already a determiner. [Filters.indefinite] owns the article in both
     * directions, so a rule that took the wrong one would print "another a Beast".
     */
    private fun filteredTriggerRule(
        surface: String,
        name: String,
        article: Boolean,
        spec: (GameObjectFilter) -> TriggerSpec,
    ): Phrase<TriggeredAbility> =
        phrase("$surface, {effect}", name = name) {
            slot("filter", if (article) Filters.indefinite else Filters.filter)
            slot("effect", Steps.step)
            build { abilityFor(spec(it.value("filter")), it.value("effect")) }
            match { ability ->
                val filter = triggeredFilter(ability) ?: return@match null
                val script = scriptFor(ability)
                if (abilityFor(spec(filter), script)?.copy(id = ability.id) != ability) return@match null
                bind("filter" to filter, "effect" to script)
            }
        }

    /**
     * The filter an event names, read back off the two event shapes the filtered rules produce.
     *
     * Only a *candidate*: the reconstruction in [filteredTriggerRule]'s `match` is what decides
     * whether the whole ability is this sentence, so nothing here has to check the event's other
     * fields.
     */
    private fun triggeredFilter(ability: TriggeredAbility): GameObjectFilter? =
        when (val event = ability.trigger) {
            is EventPattern.ZoneChangeEvent -> event.filter
            is EventPattern.BecomesBlockedEvent -> event.filter
            else -> null
        }

    private val rules: List<Phrase<TriggeredAbility>> = listOf(
        triggerRule("when ${Normalizer.SELF} enters", SdkTriggers.EntersBattlefield),
        triggerRule("when ${Normalizer.SELF} dies", SdkTriggers.Dies),
        triggerRule("when ${Normalizer.SELF} leaves the battlefield", SdkTriggers.LeavesBattlefield),
        triggerRule("whenever ${Normalizer.SELF} attacks", SdkTriggers.Attacks),
        triggerRule("whenever ${Normalizer.SELF} blocks", SdkTriggers.Blocks),
        triggerRule("whenever ${Normalizer.SELF} becomes blocked", SdkTriggers.BecomesBlocked),
        triggerRule(
            "whenever ${Normalizer.SELF} deals combat damage to a player",
            SdkTriggers.DealsCombatDamageToPlayer,
        ),
        triggerRule(
            "whenever ${Normalizer.SELF} deals combat damage to a creature",
            SdkTriggers.DealsCombatDamageToCreature,
        ),
        // "Whenever this creature deals combat damage, …" — no recipient clause at all, which is a
        // third event rather than a shorter spelling of either of the two above: Drinker of Sorrow
        // triggers on damage to anything.
        triggerRule(
            "whenever ${Normalizer.SELF} deals combat damage",
            SdkTriggers.dealsDamage(damageType = DamageType.Combat),
        ),
        triggerRule("whenever ${Normalizer.SELF} is dealt damage", SdkTriggers.TakesDamage),
        // Morph's payoff. "Is turned face up" is a `When` rather than a `Whenever` because it can
        // happen once to a permanent, which is the property that decides the word (see [rules]).
        triggerRule("when ${Normalizer.SELF} is turned face up", SdkTriggers.TurnedFaceUp),
        // Cycling's two triggers. `YouCycleThis` is the card's own cycling ("When you cycle this
        // card, …") and `AnyPlayerCycles` watches the table, so they are separate specs rather than
        // one with a player field — which is what the SDK says too.
        triggerRule("when you cycle ${Normalizer.SELF}", SdkTriggers.YouCycleThis),
        triggerRule("whenever a player cycles a card", SdkTriggers.AnyPlayerCycles),
        filteredTriggerRule(
            "whenever {filter} enters", "whenever a permanent enters", article = true,
        ) { SdkTriggers.entersBattlefield(it, TriggerBinding.ANY) },
        filteredTriggerRule(
            "whenever another {filter} enters", "whenever another permanent enters", article = false,
        ) { SdkTriggers.entersBattlefield(it, TriggerBinding.OTHER) },
        // "Whenever this creature or another Zombie enters" — Noxious Ghoul, Goblin Assassin. The
        // source is *in* the watched class, so the model is the plain `ANY` binding and the printed
        // "~ or another" is how Oracle spells that when the source matches the filter. It is a row
        // rather than a case inside the rule above because the two surfaces denote the same value
        // and only one of them may print: this one carries the noun the card prints.
        filteredTriggerRule(
            "whenever ${Normalizer.SELF} or another {filter} enters",
            "whenever the source or another permanent enters",
            article = false,
        ) { SdkTriggers.entersBattlefield(it, TriggerBinding.ANY) },
        filteredTriggerRule(
            "whenever {filter} becomes blocked", "whenever a creature becomes blocked", article = true,
        ) { SdkTriggers.becomesBlocked(it, TriggerBinding.ANY) },
        // "Whenever ~ or another creature dies, …" — Blood Artist, Skirk Drill Sergeant. One
        // ability with an `ANY` binding covers both halves, because the source is itself a member of
        // the watched class; the printed "~ or another" is how Oracle spells that, exactly as it is
        // in the enters rule above. Five hand-written cards write it as one ability and one writes
        // it as two, so the grammar emits the majority and the differential reports the rest.
        filteredTriggerRule(
            "whenever ${Normalizer.SELF} or another {filter} dies",
            "whenever the source or another permanent dies",
            article = false,
        ) { SdkTriggers.leavesBattlefield(filter = it, to = Zone.GRAVEYARD, binding = TriggerBinding.ANY) },
    ) + phaseRules

    val trigger: Phrase<TriggeredAbility> = oneOf("a triggered ability", rules)

    /** One trigger, lifted into the one-element list a line usually denotes. */
    private val single: Phrase<List<TriggeredAbility>> = phrase("{one}", name = "a triggered ability") {
        slot("one", trigger)
        build { listOf(it.value<TriggeredAbility>("one")) }
        match { it.singleOrNull()?.let { ability -> bind("one" to ability) } }
    }

    /**
     * "Whenever ~ attacks or blocks, …" — **two** triggered abilities from one printed sentence.
     *
     * A `TriggeredAbility` watches one event, and Oracle's "or" here joins two: attacking and
     * blocking are different events with the same payoff, so the card carries two abilities and
     * Embalmed Brawler's golden says so in a comment. That makes this the trigger side of
     * [Keywords.qualityRun] — a rule that denotes several models from one phrase — and the reason a
     * trigger *line* is a list rather than one ability.
     *
     * The two specs are a parameter rather than a slot because the joined phrase is one printed
     * form: "attacks or blocks" is not "attacks" plus a word, and no other pair is spelled this way.
     */
    private fun pairedTriggerRule(
        surface: String,
        name: String,
        specs: (GameObjectFilter?) -> List<TriggerSpec>,
        filtered: Boolean,
    ): Phrase<List<TriggeredAbility>> =
        phrase("$surface, {effect}", name = name) {
            if (filtered) slot("filter", Filters.filter)
            slot("effect", Steps.step)
            build { bindings ->
                val script = bindings.value<CardScript>("effect")
                val built = specs(if (filtered) bindings.value("filter") else null)
                    .map { abilityFor(it, script) }
                if (built.any { it == null }) null else built.filterNotNull()
            }
            match { abilities ->
                if (abilities.size != 2) return@match null
                val script = scriptFor(abilities.first())
                val filter = if (filtered) triggeredFilter(abilities[1]) ?: return@match null else null
                val rebuilt = specs(filter).map { abilityFor(it, script) }
                if (rebuilt.size != abilities.size) return@match null
                val matches = rebuilt.zip(abilities).all { (built, ability) ->
                    built?.copy(id = ability.id) == ability
                }
                if (!matches) return@match null
                bind("filter" to filter, "effect" to script)
            }
        }

    /**
     * Every trigger line, as the list of abilities it denotes.
     *
     * The alternatives take disjoint list sizes — [single] is exactly one and the paired rules are
     * exactly two — so printing is decided by the model rather than by the alternation's order, the
     * property every `oneOf` in this grammar is written to have.
     */
    val line: Phrase<List<TriggeredAbility>> = oneOf(
        "a triggered ability line",
        pairedTriggerRule(
            "whenever ${Normalizer.SELF} attacks or blocks",
            "whenever the source attacks or blocks",
            specs = { listOf(SdkTriggers.Attacks, SdkTriggers.Blocks) },
            filtered = false,
        ),
        single,
    )
}
