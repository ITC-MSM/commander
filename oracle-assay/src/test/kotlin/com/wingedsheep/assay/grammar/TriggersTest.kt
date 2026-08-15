package com.wingedsheep.assay.grammar

import com.wingedsheep.assay.syntax.ParseOutcome
import com.wingedsheep.assay.syntax.parseLine
import com.wingedsheep.assay.syntax.printLine
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.model.CardScript
import com.wingedsheep.sdk.scripting.AbilityId
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggeredAbility
import com.wingedsheep.sdk.dsl.Triggers as SdkTriggers
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * The trigger prefix: the first rules that reach a `CardScript` slot other than the spell effect,
 * and the first that depend on normalization abstracting a card's self-reference.
 */
class TriggersTest : StringSpec({

    fun fragment(line: String): CardFragment =
        Grammar.abilityLine.parseLine(line).shouldBeInstanceOf<ParseOutcome.Accepted<CardFragment>>().value

    fun roundTrips(line: String) {
        Grammar.abilityLine.printLine(fragment(line)) shouldBe line
    }

    fun ability(line: String): TriggeredAbility =
        fragment(line).script.triggeredAbilities.single()

    // Kavu Climber's golden is this model exactly, down to the trigger's serialized shape — which
    // is the point of parsing into `mtg-sdk` types rather than into an IR of our own.
    "an ETB trigger is the ability a card author writes from the same sentence" {
        ability("When ~ enters, draw a card.") shouldBe TriggeredAbility(
            id = AbilityId("trigger"),
            trigger = SdkTriggers.EntersBattlefield.event,
            binding = SdkTriggers.EntersBattlefield.binding,
            effect = Effects.DrawCards(1),
        )
        roundTrips("When ~ enters, draw a card.")
    }

    "the effect clause is the whole step vocabulary, not a second grammar" {
        listOf(
            "When ~ enters, draw two cards.",
            "When ~ enters, destroy target creature.",
            "When ~ enters, exile target artifact or enchantment.",
            "When ~ dies, draw a card.",
            "Whenever ~ attacks, draw a card.",
            "Whenever ~ blocks, tap target creature an opponent controls.",
            "Whenever ~ deals combat damage to a player, draw a card.",
        ).forEach { roundTrips(it) }
    }

    // A `TriggeredAbility` keeps its target on the ability rather than on the script, so the lift
    // out of `Steps` has to move it there — and the differential compares the result against cards
    // that write `target("target", …)` inside `triggeredAbility { }`.
    "a targeted trigger declares its requirement on the ability" {
        val triggered = ability("When ~ enters, destroy target creature.")

        triggered.targetRequirement shouldBe Targets.permanent(GameObjectFilter.Creature)
        triggered.effect shouldBe Effects.Destroy(Targets.bound())
        fragment("When ~ enters, destroy target creature.").script.targetRequirements shouldBe emptyList()
    }

    "several trigger lines are several abilities, in printed order" {
        val first = fragment("When ~ enters, draw a card.")
        val second = fragment("Whenever ~ attacks, draw two cards.")
        val whole = first.merge(second)

        whole?.script?.triggeredAbilities?.map { it.trigger } shouldBe listOf(
            SdkTriggers.EntersBattlefield.event,
            SdkTriggers.Attacks.event,
        )
    }

    // Fail-closed, the same rule the step matchers follow: an ability carrying anything the sentence
    // does not spell must refuse to print rather than print a sentence that drops it.
    "an ability with content the prefix does not spell refuses to print" {
        val optional = TriggeredAbility(
            id = AbilityId("trigger"),
            trigger = SdkTriggers.EntersBattlefield.event,
            binding = SdkTriggers.EntersBattlefield.binding,
            effect = Effects.DrawCards(1),
            optional = true,
        )

        Grammar.abilityLine.printLine(
            CardFragment(script = CardScript(triggeredAbilities = listOf(optional)))
        ) shouldBe null
    }

    // The id is not in the text, so it must not stop a card's own ability from printing — the one
    // field the fail-closed comparison deliberately exempts.
    "an ability's arbitrary id does not stop it printing" {
        val theirs = TriggeredAbility(
            id = AbilityId("ability_1"),
            trigger = SdkTriggers.EntersBattlefield.event,
            binding = SdkTriggers.EntersBattlefield.binding,
            effect = Effects.DrawCards(1),
        )

        Grammar.abilityLine.printLine(
            CardFragment(script = CardScript(triggeredAbilities = listOf(theirs)))
        ) shouldBe "When ~ enters, draw a card."
    }
})
