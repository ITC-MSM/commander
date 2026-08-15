package com.wingedsheep.assay.grammar

import com.wingedsheep.assay.syntax.ParseOutcome
import com.wingedsheep.assay.syntax.parseLine
import com.wingedsheep.assay.syntax.printLine
import com.wingedsheep.sdk.dsl.Conditions
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

    "the step triggers are the same shape with a different prefix" {
        listOf(
            "At the beginning of your upkeep, draw a card.",
            "At the beginning of your end step, you gain 2 life.",
            "At the beginning of combat on your turn, target creature gets +1/+1 until end of turn.",
            "At the beginning of your first main phase, scry 1.",
            "At the beginning of each upkeep, ~ deals 1 damage to any target.",
            "At the beginning of each opponent's upkeep, you gain 1 life.",
        ).forEach { roundTrips(it) }

        ability("At the beginning of your upkeep, draw a card.").trigger shouldBe
            SdkTriggers.YourUpkeep.event
    }

    // Wizards templates the all-players steps both ways. One model cannot have two printed forms, so
    // the more common spelling prints and the other parses — VARIANT, not a decline, and the reading
    // is provably unchanged because reparsing the printed line gives the identical ability.
    "the each-player spelling parses to the same model and prints as the canonical one" {
        ability("At the beginning of each player's upkeep, draw a card.") shouldBe
            ability("At the beginning of each upkeep, draw a card.")

        Grammar.abilityLine.printLine(fragment("At the beginning of each player's upkeep, draw a card.")) shouldBe
            "At the beginning of each upkeep, draw a card."
    }

    // "You may …" is not unspellable content: a triggered ability's `optional` flag and a spell's
    // `MayEffect` are two SDK spellings of one sentence, and `Triggers.abilityFor` lowers between
    // them so the printed form is the same either way.
    "the optional flag is the trigger's spelling of \"you may\"" {
        val optional = TriggeredAbility(
            id = AbilityId("trigger"),
            trigger = SdkTriggers.EntersBattlefield.event,
            binding = SdkTriggers.EntersBattlefield.binding,
            effect = Effects.DrawCards(1),
            optional = true,
        )

        Grammar.abilityLine.printLine(
            CardFragment(script = CardScript(triggeredAbilities = listOf(optional)))
        ) shouldBe "When ~ enters, you may draw a card."
        fragment("When ~ enters, you may draw a card.") shouldBe
            CardFragment(script = CardScript(triggeredAbilities = listOf(optional)))
    }

    // An intervening-if (CR 603.4) *is* spellable: it is the clause between the event and the
    // effect, and `triggerCondition` is the SDK's slot for it. `Triggers.abilityFor` lifts the
    // clause's own gate into that slot rather than leaving a copy in the effect, which is what 478
    // hand-written cards do and what keeps one printed form for one model.
    "an intervening-if is the trigger's own condition, not a second gate in the effect" {
        val conditioned = TriggeredAbility(
            id = AbilityId("trigger"),
            trigger = SdkTriggers.EntersBattlefield.event,
            binding = SdkTriggers.EntersBattlefield.binding,
            effect = Effects.DrawCards(1),
            triggerCondition = Conditions.OpponentControlsMoreLands,
        )

        fragment("When ~ enters, if an opponent controls more lands than you, draw a card.") shouldBe
            CardFragment(script = CardScript(triggeredAbilities = listOf(conditioned)))
        Grammar.abilityLine.printLine(
            CardFragment(script = CardScript(triggeredAbilities = listOf(conditioned)))
        ) shouldBe "When ~ enters, if an opponent controls more lands than you, draw a card."
    }

    // Fail-closed, the same rule the step matchers follow: an ability carrying anything the sentence
    // does not spell must refuse to print rather than print a sentence that drops it. A
    // once-per-turn cap is the example — no trigger rule spells one.
    "an ability with content the prefix does not spell refuses to print" {
        val capped = TriggeredAbility(
            id = AbilityId("trigger"),
            trigger = SdkTriggers.EntersBattlefield.event,
            binding = SdkTriggers.EntersBattlefield.binding,
            effect = Effects.DrawCards(1),
            oncePerTurn = true,
        )

        Grammar.abilityLine.printLine(
            CardFragment(script = CardScript(triggeredAbilities = listOf(capped)))
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
