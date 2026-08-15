package com.wingedsheep.assay.grammar

import com.wingedsheep.assay.normalize.Normalizer
import com.wingedsheep.assay.syntax.Phrase
import com.wingedsheep.assay.syntax.alternate
import com.wingedsheep.assay.syntax.bind
import com.wingedsheep.assay.syntax.oneOf
import com.wingedsheep.assay.syntax.phrase
import com.wingedsheep.sdk.model.CardScript
import com.wingedsheep.sdk.scripting.AbilityId
import com.wingedsheep.sdk.scripting.effects.Gate
import com.wingedsheep.sdk.scripting.effects.GatedEffect
import com.wingedsheep.sdk.scripting.effects.MayEffect
import com.wingedsheep.sdk.scripting.TriggerSpec
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
        if (script.targetRequirements.size > 1) return null
        val gated = effect as? GatedEffect
        val optional = gated != null && gated.gate is Gate.MayDecide &&
            gated == MayEffect(gated.then)
        return TriggeredAbility(
            id = ID,
            trigger = spec.event,
            binding = spec.binding,
            effect = if (optional) (effect as GatedEffect).then else effect,
            targetRequirement = script.targetRequirements.singleOrNull(),
            optional = optional,
        )
    }

    /** The inverse of the lowering: the clause script an ability's effect and target denote. */
    private fun scriptFor(ability: TriggeredAbility): CardScript = CardScript(
        spellEffect = if (ability.optional) MayEffect(ability.effect) else ability.effect,
        targetRequirements = listOfNotNull(ability.targetRequirement),
    )

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
    private val phaseRules: List<Phrase<TriggeredAbility>> = listOf(
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
    ) + phaseRules

    val trigger: Phrase<TriggeredAbility> = oneOf("a triggered ability", rules)
}
