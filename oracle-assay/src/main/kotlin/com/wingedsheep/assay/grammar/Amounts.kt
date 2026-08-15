package com.wingedsheep.assay.grammar

import com.wingedsheep.assay.normalize.Normalizer
import com.wingedsheep.assay.syntax.Phrase
import com.wingedsheep.assay.syntax.bind
import com.wingedsheep.assay.syntax.constant
import com.wingedsheep.assay.syntax.oneOf
import com.wingedsheep.assay.syntax.phrase
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.model.CardScript
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.events.CounterTypeFilter
import com.wingedsheep.sdk.scripting.effects.CompositeEffect
import com.wingedsheep.sdk.scripting.effects.DealDamageEffect
import com.wingedsheep.sdk.scripting.effects.DrawCardsEffect
import com.wingedsheep.sdk.scripting.effects.Effect
import com.wingedsheep.sdk.scripting.effects.GainLifeEffect
import com.wingedsheep.sdk.scripting.effects.LoseLifeEffect
import com.wingedsheep.sdk.scripting.effects.ModifyStatsEffect
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.ContextPropertyKey
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Clauses whose number is a **count of something**, and the vocabulary that names the count.
 *
 * Oracle spells a variable quantity three ways, and all three are here because they are one family
 * in the model — a [DynamicAmount] in the slot a numeral would otherwise fill:
 *
 * | Surface | Example |
 * |---|---|
 * | a trailing definition of X | "gets +X/+X until end of turn, **where X is the number of Elves on the battlefield**" |
 * | "equal to …" | "deals damage **equal to the number of +1/+1 counters on it**" |
 * | "for each …" | "you lose 1 life **for each +1/+1 counter on it**" |
 *
 * ### Why the amount is a slot and the verb is not
 *
 * [Steps.countedStep] takes a numeral and [Steps.amountStep] takes a *fixed* dynamic amount chosen
 * by the rule, because in those sentences the amount has no printed form of its own — "X" is a
 * literal. Here it does: "the number of Elves on the battlefield" is a whole noun phrase that varies
 * independently of the verb, so it is a phrase ([count]) and the verbs slot it. That is what makes
 * this a family of two dozen sentences rather than a rule per tribe.
 *
 * ### The where-clause is part of the sentence, not of the amount
 *
 * "…gets +X/+X until end of turn, where X is the number of Elves on the battlefield" is *one*
 * sentence: the "X" in the verb phrase and the "X" in the definition are the same value, and the
 * model stores it once. So the rule spans both halves and the literal "X" appears twice in the
 * template — a card that printed the definition without the use, or two different letters, is not
 * this sentence and declines.
 */
object Amounts {

    // ---------------------------------------------------------------------------------------
    // The vocabulary: what a count counts
    // ---------------------------------------------------------------------------------------

    /**
     * "the number of Elves on the battlefield", "the number of Zombies you control" — a battlefield
     * tally over a noun phrase.
     *
     * The two surfaces differ only in whose battlefield is scanned, which is the `Player` field of
     * the aggregate, so they are two rows of one shape rather than a rule with a player slot: "on
     * the battlefield" and "you control" are not two values of one word, they are two clauses.
     */
    private fun battlefieldCount(surface: String, player: Player, name: String): Phrase<DynamicAmount> =
        phrase("the number of {filter} $surface", name = name) {
            slot("filter", Filters.plural)
            build { DynamicAmount.AggregateBattlefield(player, it.value("filter")) }
            match { amount ->
                val aggregate = amount as? DynamicAmount.AggregateBattlefield ?: return@match null
                if (aggregate.player != player) return@match null
                if (amount != DynamicAmount.AggregateBattlefield(player, aggregate.filter)) return@match null
                bind("filter" to aggregate.filter)
            }
        }

    /**
     * Everything a "where X is …" clause can define.
     *
     * "the number of cards in their hand" is a *zone* count rather than a battlefield one and its
     * player is the triggering one, so it is a constant here: nothing in it varies, and a rule with
     * a zone slot would be a vocabulary for a phrase Oracle spells exactly one way.
     */
    val count: Phrase<DynamicAmount> = oneOf(
        "a count",
        battlefieldCount("on the battlefield", Player.Each, "a count of the whole battlefield"),
        battlefieldCount("you control", Player.You, "a count of your battlefield"),
        constant(
            "the number of cards in their hand",
            DynamicAmount.Count(Player.TriggeringPlayer, Zone.HAND),
        ),
    )

    /** "+1/+1 counters on it" / "+1/+1 counter on ~" — a tally of the source's own counters. */
    private val plusOneCounters: DynamicAmount = DynamicAmounts.countersOnSelf(CounterTypeFilter.PlusOnePlusOne)

    // ---------------------------------------------------------------------------------------
    // The clauses
    // ---------------------------------------------------------------------------------------

    /**
     * "Target creature gets +X/+X until end of turn, where X is the number of Elves on the
     * battlefield." — Timberwatch Elf, and Magma Sliver's granted "+X/+0" sibling.
     *
     * The *shape* of the modifier is the rule's parameter rather than a slot, because "+X/+X" and
     * "+X/+0" are two printed forms in which the letter appears a different number of times;
     * [Primitives.statModifiers] reads numerals and cannot spell either.
     */
    private fun pumpTargetByCount(
        modifier: String,
        toughness: (DynamicAmount) -> DynamicAmount,
        name: String,
    ): Phrase<CardScript> {
        fun scriptFor(amount: DynamicAmount, filter: GameObjectFilter) = CardScript(
            spellEffect = Effects.ModifyStats(amount, toughness(amount), Targets.bound()),
            targetRequirements = listOf(Targets.permanent(filter)),
        )
        return phrase("target {filter} gets $modifier until end of turn, where X is {amount}", name = name) {
            slot("filter", Filters.filter)
            slot("amount", count)
            build { scriptFor(it.value("amount"), it.value("filter")) }
            match { script ->
                val stats = script.spellEffect as? ModifyStatsEffect ?: return@match null
                val requirement = script.targetRequirements.singleOrNull() ?: return@match null
                val filter = Targets.permanentFilter(requirement) ?: return@match null
                if (script != scriptFor(stats.powerModifier, filter)) return@match null
                bind("filter" to filter, "amount" to stats.powerModifier)
            }
        }
    }

    /** "It gets +X/+X until end of turn, where X is the number of Clerics on the battlefield." */
    private val pumpSelfByCount: Phrase<CardScript> = run {
        fun scriptFor(amount: DynamicAmount) =
            CardScript(spellEffect = Effects.ModifyStats(amount, amount, EffectTarget.Self))
        phrase(
            "{self} gets +X/+X until end of turn, where X is {amount}",
            name = "the source gets a count",
        ) {
            slot("self", Primitives.self)
            slot("amount", count)
            build { scriptFor(it.value("amount")) }
            match { script ->
                val stats = script.spellEffect as? ModifyStatsEffect ?: return@match null
                if (script != scriptFor(stats.powerModifier)) return@match null
                bind("self" to Unit, "amount" to stats.powerModifier)
            }
        }
    }

    /**
     * "All creatures get -X/-X until end of turn." — Bane of the Living's morph payoff.
     *
     * `X` here is the spell's own `{X}`, not a count, so there is no where-clause and the amount is
     * a constant of the rule. The negation lives in the model as a `Multiply` by −1, which is what
     * makes the printed minus sign recoverable: a card printing "+X/+X" is the same rule with the
     * multiplier absent, and it declines here rather than losing the sign.
     */
    private fun groupPumpByX(prefix: String, name: String): Phrase<CardScript> {
        val amount = DynamicAmount.Multiply(DynamicAmount.XValue, -1)
        fun scriptFor(filter: GameObjectFilter) = CardScript(
            spellEffect = Effects.ForEachInGroup(
                GroupFilter(filter),
                Effects.ModifyStats(amount, amount, EffectTarget.Self),
            )
        )
        return phrase("$prefix{filter} get -X/-X until end of turn", name = name) {
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
     * "You draw X cards and you lose X life, where X is the number of Zombies you control." —
     * Graveborn Muse.
     *
     * One where-clause and two uses of it, which is why this is a rule rather than a
     * [Steps.sequence] of two: the sentence defines X once at its end, so neither half is a sentence
     * on its own and the join is "and" rather than a full stop.
     */
    private val drawAndLoseByCount: Phrase<CardScript> = run {
        fun scriptFor(amount: DynamicAmount) = CardScript(
            spellEffect = Effects.Composite(
                listOf(
                    Effects.DrawCards(amount, EffectTarget.Controller),
                    Effects.LoseLife(amount, EffectTarget.Controller),
                )
            )
        )
        phrase(
            "you draw X cards and you lose X life, where X is {amount}",
            name = "draw and lose a count",
        ) {
            slot("amount", count)
            build { scriptFor(it.value("amount")) }
            match { script ->
                val effects = (script.spellEffect as? CompositeEffect)?.effects ?: return@match null
                val amount = (effects.firstOrNull() as? DrawCardsEffect)?.count ?: return@match null
                if (script != scriptFor(amount)) return@match null
                bind("amount" to amount)
            }
        }
    }

    /** "That player mills X cards, where X is the number of cards in their hand." — Dreamborn Muse. */
    private val triggeringPlayerMillsByCount: Phrase<CardScript> = run {
        fun scriptFor(amount: DynamicAmount) = CardScript(
            spellEffect = Patterns.Library.mill(amount, EffectTarget.PlayerRef(Player.TriggeringPlayer))
        )
        phrase("that player mills X cards, where X is {amount}", name = "the triggering player mills a count") {
            slot("amount", count)
            build { scriptFor(it.value("amount")) }
            match { script ->
                val amount = milledCount(script.spellEffect) ?: return@match null
                if (script != scriptFor(amount)) return@match null
                bind("amount" to amount)
            }
        }
    }

    /** "Add X mana of any one color, where X is the number of Elves on the battlefield." */
    private val addManaByCount: Phrase<CardScript> = run {
        fun scriptFor(amount: DynamicAmount) = CardScript(spellEffect = Effects.AddAnyColorMana(amount))
        phrase("add X mana of any one color, where X is {amount}", name = "add a count of mana") {
            slot("amount", count)
            build { scriptFor(it.value("amount")) }
            match { script ->
                val amount = addedManaAmount(script.spellEffect) ?: return@match null
                if (script != scriptFor(amount)) return@match null
                bind("amount" to amount)
            }
        }
    }

    /** "Draw a card for each Wizard you control." — Riptide Director. */
    private val drawForEachYouControl: Phrase<CardScript> = run {
        fun scriptFor(filter: GameObjectFilter) = CardScript(
            spellEffect = Effects.DrawCards(DynamicAmount.AggregateBattlefield(Player.You, filter))
        )
        phrase("draw a card for each {filter} you control", name = "draw for each you control") {
            slot("filter", Filters.filter)
            build { scriptFor(it.value("filter")) }
            match { script ->
                val amount = (script.spellEffect as? DrawCardsEffect)?.count
                    as? DynamicAmount.AggregateBattlefield ?: return@match null
                if (amount.player != Player.You) return@match null
                if (script != scriptFor(amount.filter)) return@match null
                bind("filter" to amount.filter)
            }
        }
    }

    // ---------------------------------------------------------------------------------------
    // Counts of the source's own counters
    // ---------------------------------------------------------------------------------------

    /**
     * "~ deals damage equal to the number of +1/+1 counters on it to any target." — Kilnmouth
     * Dragon, Daru Stinger.
     *
     * The recipient is a rule parameter rather than a slot for [Steps.countedStep]'s reason: "any
     * target" and "target attacking or blocking creature" are different clauses with different
     * requirements, not two values of one word.
     */
    private fun damageByOwnCounters(
        recipient: String,
        name: String,
        requirement: com.wingedsheep.sdk.scripting.targets.TargetRequirement?,
        filtered: Boolean,
    ): Phrase<CardScript> {
        fun scriptFor(target: com.wingedsheep.sdk.scripting.targets.TargetRequirement) = CardScript(
            spellEffect = Effects.DealDamage(plusOneCounters, Targets.bound()),
            targetRequirements = listOf(target),
        )
        return phrase(
            "{self} deals damage equal to the number of +1/+1 counters on it to $recipient",
            name = name,
        ) {
            slot("self", Primitives.self)
            if (filtered) slot("filter", Filters.filter)
            build { bindings ->
                val target = requirement ?: Targets.permanent(bindings.value("filter"))
                scriptFor(target)
            }
            match { script ->
                val target = script.targetRequirements.singleOrNull() ?: return@match null
                if ((script.spellEffect as? DealDamageEffect)?.amount != plusOneCounters) return@match null
                if (script != scriptFor(target)) return@match null
                if (requirement != null) {
                    if (target != requirement) return@match null
                    bind("self" to Unit)
                } else {
                    val filter = Targets.permanentFilter(target) ?: return@match null
                    bind("self" to Unit, "filter" to filter)
                }
            }
        }
    }

    /** "Target creature gets +1/+1 until end of turn for each +1/+1 counter on ~." — Canopy Crawler. */
    private val pumpTargetPerOwnCounter: Phrase<CardScript> = run {
        fun scriptFor(filter: GameObjectFilter) = CardScript(
            spellEffect = Effects.ModifyStats(plusOneCounters, plusOneCounters, Targets.bound()),
            targetRequirements = listOf(Targets.permanent(filter)),
        )
        phrase(
            "target {filter} gets +1/+1 until end of turn for each +1/+1 counter on {self}",
            name = "pump a target per counter on the source",
        ) {
            slot("filter", Filters.filter)
            slot("self", Primitives.self)
            build { scriptFor(it.value("filter")) }
            match { script ->
                val requirement = script.targetRequirements.singleOrNull() ?: return@match null
                val filter = Targets.permanentFilter(requirement) ?: return@match null
                if (script != scriptFor(filter)) return@match null
                bind("filter" to filter, "self" to Unit)
            }
        }
    }

    /** "You lose 1 life for each +1/+1 counter on it." — Embalmed Brawler. */
    private val loseLifePerOwnCounter: Phrase<CardScript> = run {
        val script = CardScript(spellEffect = Effects.LoseLife(plusOneCounters, EffectTarget.Controller))
        phrase("you lose 1 life for each +1/+1 counter on it", name = "lose life per counter on the source") {
            build { script }
            match { if (it == script) bind() else null }
        }
    }

    /**
     * "You gain that much life." — Wall of Hope, after a damage trigger.
     *
     * "That much" is the damage the trigger reported, which the SDK names as a context property
     * rather than as a count of anything on the battlefield. It is a clause of its own rather than a
     * member of [count] because the phrase replaces the *whole* amount and has no "the number of…"
     * shape to slot.
     */
    private val gainThatMuchLife: Phrase<CardScript> = run {
        val script = CardScript(
            spellEffect = Effects.GainLife(
                DynamicAmount.ContextProperty(ContextPropertyKey.TRIGGER_DAMAGE_AMOUNT),
                EffectTarget.Controller,
            )
        )
        phrase("you gain that much life", name = "gain that much life") {
            build { script }
            match { if (it == script) bind() else null }
        }
    }

    /**
     * "It gets +1/+1 until end of turn for each creature blocking it." — Berserk Murlodont.
     *
     * The "it" is the creature the *trigger* named, not the source and not a target: a Beast that
     * became blocked. So the reference is `EffectTarget.TriggeringEntity` and the count is the same
     * entity's blocker tally — one printed pronoun standing for one object in two places, which is
     * why nothing here is a slot.
     */
    private val triggeringGetsPerBlocker: Phrase<CardScript> = run {
        val blockers = DynamicAmounts.numberOfBlockers()
        val script = CardScript(
            spellEffect = Effects.ModifyStats(blockers, blockers, EffectTarget.TriggeringEntity)
        )
        phrase(
            "it gets +1/+1 until end of turn for each creature blocking it",
            name = "the triggering creature gets per blocker",
        ) {
            build { script }
            match { if (it == script) bind() else null }
        }
    }

    /**
     * "You may have target player lose life equal to the number of Zombies on the battlefield." —
     * Gempalm Polluter — and Gempalm Incinerator's damage sibling.
     *
     * "You may have <someone> <verb>" is Oracle's causative: the controller chooses, and the thing
     * that happens is aimed at the target. The model is the plain optional wrapper around the verb,
     * so what the phrasing buys is nothing the model needs — which is why the whole sentence is the
     * rule and only the amount and the noun phrase are slots.
     */
    private fun mayHaveTargetSuffer(
        template: String,
        name: String,
        requirement: () -> com.wingedsheep.sdk.scripting.targets.TargetRequirement,
        filtered: Boolean,
        effect: (DynamicAmount) -> Effect,
    ): Phrase<CardScript> {
        fun scriptFor(amount: DynamicAmount, target: com.wingedsheep.sdk.scripting.targets.TargetRequirement) =
            CardScript(
                spellEffect = com.wingedsheep.sdk.scripting.effects.MayEffect(effect(amount)),
                targetRequirements = listOf(target),
            )
        return phrase(template, name = name) {
            if (filtered) slot("filter", Filters.filter)
            slot("amount", count)
            build { bindings ->
                val target = if (filtered) Targets.permanent(bindings.value("filter")) else requirement()
                scriptFor(bindings.value("amount"), target)
            }
            match { script ->
                val gated = script.spellEffect as? com.wingedsheep.sdk.scripting.effects.GatedEffect
                    ?: return@match null
                val amount = amountOf(gated.then) ?: return@match null
                val target = script.targetRequirements.singleOrNull() ?: return@match null
                if (script != scriptFor(amount, target)) return@match null
                if (filtered) {
                    val filter = Targets.permanentFilter(target) ?: return@match null
                    bind("filter" to filter, "amount" to amount)
                } else {
                    if (target != requirement()) return@match null
                    bind("amount" to amount)
                }
            }
        }
    }

    /** The amount a life-loss or damage effect carries, whichever of the two it is. */
    private fun amountOf(effect: Effect): DynamicAmount? = when (effect) {
        is LoseLifeEffect -> effect.amount
        is DealDamageEffect -> effect.amount
        else -> null
    }

    val clauses: List<Phrase<CardScript>> = listOf(
        pumpTargetByCount("+X/+X", { it }, "pump a target by a count"),
        pumpTargetByCount("+X/+0", { DynamicAmount.Fixed(0) }, "pump a target's power by a count"),
        pumpSelfByCount,
        groupPumpByX("", "a group gets minus X"),
        groupPumpByX("all ", "all of a group gets minus X"),
        drawAndLoseByCount,
        triggeringPlayerMillsByCount,
        addManaByCount,
        drawForEachYouControl,
        pumpTargetPerOwnCounter,
        loseLifePerOwnCounter,
        gainThatMuchLife,
        damageByOwnCounters("any target", "damage by own counters to any target", Targets.any(), filtered = false),
        damageByOwnCounters("target {filter}", "damage by own counters to a target", null, filtered = true),
        triggeringGetsPerBlocker,
        mayHaveTargetSuffer(
            "you may have target player lose life equal to {amount}",
            "you may have a player lose life",
            requirement = { Targets.player() },
            filtered = false,
        ) { Effects.LoseLife(it, Targets.bound()) },
        mayHaveTargetSuffer(
            "you may have it deal X damage to target {filter}, where X is {amount}",
            "you may have the source deal damage",
            requirement = { Targets.any() },
            filtered = true,
        ) { Effects.DealDamage(it, Targets.bound()) },
    )

    // ---------------------------------------------------------------------------------------
    // Model helpers
    // ---------------------------------------------------------------------------------------

    private fun iteratedGroup(effect: Effect?): GameObjectFilter? {
        val forEach = effect as? com.wingedsheep.sdk.scripting.effects.ForEachEffect ?: return null
        val space = forEach.space as? com.wingedsheep.sdk.scripting.effects.IterationSpace.Group ?: return null
        return space.filter.baseFilter
    }

    /** The count a `Patterns.Library.mill` pipeline moves, read off its gather step. */
    private fun milledCount(effect: Effect?): DynamicAmount? {
        val gather = (effect as? CompositeEffect)?.effects?.firstOrNull()
            as? com.wingedsheep.sdk.scripting.effects.GatherCardsEffect ?: return null
        return (gather.source as? com.wingedsheep.sdk.scripting.effects.CardSource.TopOfLibrary)?.count
    }

    private fun addedManaAmount(effect: Effect?): DynamicAmount? =
        (effect as? com.wingedsheep.sdk.scripting.effects.AddManaOfChoiceEffect)?.amount
}
