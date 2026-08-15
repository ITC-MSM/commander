package com.wingedsheep.assay.grammar

import com.wingedsheep.assay.normalize.Normalizer
import com.wingedsheep.assay.syntax.Phrase
import com.wingedsheep.assay.syntax.bind
import com.wingedsheep.assay.syntax.oneOf
import com.wingedsheep.assay.syntax.phrase
import com.wingedsheep.sdk.model.CardScript
import com.wingedsheep.sdk.scripting.AbilityId
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
        fun abilityFor(script: CardScript): TriggeredAbility? {
            val effect = script.spellEffect ?: return null
            if (script.targetRequirements.size > 1) return null
            return TriggeredAbility(
                id = ID,
                trigger = spec.event,
                binding = spec.binding,
                effect = effect,
                targetRequirement = script.targetRequirements.singleOrNull(),
            )
        }

        return phrase("$surface, {effect}", name = surface) {
            slot("effect", Steps.step)
            build { abilityFor(it.value("effect")) }
            match { ability ->
                val script = CardScript(
                    spellEffect = ability.effect,
                    targetRequirements = listOfNotNull(ability.targetRequirement),
                )
                if (abilityFor(script)?.copy(id = ability.id) != ability) return@match null
                bind("effect" to script)
            }
        }
    }

    /**
     * The trigger events with an unambiguous one-clause surface form.
     *
     * "When" versus "Whenever" is a property of the event rather than a choice: an event that
     * happens once to a permanent is templated "When", a repeatable one "Whenever". Baking the word
     * into each rule is what keeps one printed form per model.
     */
    private val rules: List<Phrase<TriggeredAbility>> = listOf(
        triggerRule("when ${Normalizer.SELF} enters", SdkTriggers.EntersBattlefield),
        triggerRule("when ${Normalizer.SELF} dies", SdkTriggers.Dies),
        triggerRule("whenever ${Normalizer.SELF} attacks", SdkTriggers.Attacks),
        triggerRule("whenever ${Normalizer.SELF} blocks", SdkTriggers.Blocks),
        triggerRule(
            "whenever ${Normalizer.SELF} deals combat damage to a player",
            SdkTriggers.DealsCombatDamageToPlayer,
        ),
    )

    val trigger: Phrase<TriggeredAbility> = oneOf("a triggered ability", rules)
}
