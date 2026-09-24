package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EventPattern
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.TriggerSpec
import com.wingedsheep.sdk.scripting.effects.ForEachTargetEffect
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetObject
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * The Theorist, Jace Beleren
 * {2}{U}{U}
 * Legendary Planeswalker — Jace
 * Starting Loyalty: 3
 *
 * - "At the beginning of each opponent's draw step" is a step trigger scoped to
 *   [Player.EachOpponent] — it fires on every opponent's draw step, and the draw goes to Jace's
 *   controller.
 * - −2 is Kaya, Spirits' Justice's one-per-player distribution: up to one artifact or creature per
 *   opponent (`dynamicMaxCount = PlayerCount(EachOpponent)`, `differentControllers = true`), each
 *   returned to its owner's hand through [ForEachTargetEffect].
 * - −6 reads X after the three draws: the counters are placed per creature with X evaluated at
 *   that point, which is the "then".
 */
val TheTheoristJaceBeleren = card("The Theorist, Jace Beleren") {
    manaCost = "{2}{U}{U}"
    colorIdentity = "U"
    typeLine = "Legendary Planeswalker — Jace"
    startingLoyalty = 3
    oracleText = "At the beginning of each opponent's draw step, you draw a card.\n" +
        "+1: Create a 1/1 blue Illusion creature token.\n" +
        "−2: For each opponent, return up to one target artifact or creature that player controls " +
        "to its owner's hand.\n" +
        "−6: Draw three cards. Then put X +1/+1 counters on each creature you control, where X is " +
        "the number of cards in your hand."

    triggeredAbility {
        trigger = TriggerSpec(
            event = EventPattern.StepEvent(Step.DRAW, Player.EachOpponent),
            binding = TriggerBinding.ANY,
        )
        effect = Effects.DrawCards(1)
        description = "At the beginning of each opponent's draw step, you draw a card."
    }

    loyaltyAbility(+1) {
        effect = Effects.CreateToken(
            power = 1,
            toughness = 1,
            colors = setOf(Color.BLUE),
            creatureTypes = setOf("Illusion"),
            imageUri = "https://cards.scryfall.io/normal/front/7/b/7bda0b63-fd0f-495d-9eb0-20057571c0ee.jpg?1787952525",
        )
        description = "Create a 1/1 blue Illusion creature token."
    }

    loyaltyAbility(-2) {
        target(
            "up to one target artifact or creature each opponent controls",
            TargetObject(
                filter = TargetFilter.CreatureOrArtifact.opponentControls(),
                optional = true,
                dynamicMaxCount = DynamicAmount.PlayerCount(Player.EachOpponent),
                differentControllers = true,
            ),
        )
        effect = ForEachTargetEffect(
            listOf(Effects.ReturnToHand(EffectTarget.ContextTarget(0)))
        )
        description = "For each opponent, return up to one target artifact or creature that player " +
            "controls to its owner's hand."
    }

    loyaltyAbility(-6) {
        effect = Effects.Composite(
            Effects.DrawCards(3),
            Effects.ForEachInGroup(
                GroupFilter.AllCreaturesYouControl,
                Effects.AddDynamicCounters(
                    Counters.PLUS_ONE_PLUS_ONE,
                    DynamicAmounts.cardsInYourHand(),
                    EffectTarget.Self,
                ),
            ),
        )
        description = "Draw three cards. Then put X +1/+1 counters on each creature you control, " +
            "where X is the number of cards in your hand."
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "43"
        artist = "Ekaterina Burmak"
        imageUri = "https://cards.scryfall.io/normal/front/2/0/20bb8c55-4b0b-425f-8201-b54fa2fdde86.jpg?1788329228"
        inBooster = false
    }
}
