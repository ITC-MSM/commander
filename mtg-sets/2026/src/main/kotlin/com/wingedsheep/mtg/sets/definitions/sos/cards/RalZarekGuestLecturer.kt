package com.wingedsheep.mtg.sets.definitions.sos.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.ForEachTargetEffect
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetObject
import com.wingedsheep.sdk.scripting.targets.TargetPlayer
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Ral Zarek, Guest Lecturer
 * {1}{B}{B}
 * Legendary Planeswalker — Ral
 * Starting loyalty: 3
 *
 * +1: Surveil 2.
 * −1: Any number of target players each discard a card.
 * −2: Return target creature card with mana value 3 or less from your graveyard to the battlefield.
 * −7: Flip five coins. Target opponent skips their next X turns, where X is the number of coins
 *     that came up heads.
 *
 * The ultimate composes two general primitives: [Effects.FlipCoins] tallies the heads into the
 * pipeline, and [Effects.SkipNextTurn] reads that tally as its turn count — so a result of zero
 * heads is a clean no-op.
 */
val RalZarekGuestLecturer = card("Ral Zarek, Guest Lecturer") {
    manaCost = "{1}{B}{B}"
    colorIdentity = "B"
    typeLine = "Legendary Planeswalker — Ral"
    startingLoyalty = 3
    oracleText = "+1: Surveil 2.\n" +
        "−1: Any number of target players each discard a card.\n" +
        "−2: Return target creature card with mana value 3 or less from your graveyard to " +
        "the battlefield.\n" +
        "−7: Flip five coins. Target opponent skips their next X turns, where X is the number " +
        "of coins that came up heads."

    // +1: Surveil 2.
    loyaltyAbility(+1) {
        effect = Patterns.Library.surveil(2)
    }

    // −1: Any number of target players each discard a card.
    loyaltyAbility(-1) {
        target = TargetPlayer(unlimited = true)
        effect = ForEachTargetEffect(
            listOf(Effects.Discard(1, EffectTarget.ContextTarget(0)))
        )
    }

    // −2: Return target creature card with mana value 3 or less from your graveyard to the battlefield.
    loyaltyAbility(-2) {
        val creature = target(
            "creature",
            TargetObject(filter = TargetFilter.CreatureInYourGraveyard.manaValueAtMost(3)),
        )
        effect = Effects.Move(creature, Zone.BATTLEFIELD, fromZone = Zone.GRAVEYARD)
    }

    // −7: Flip five coins. Target opponent skips their next X turns, where X is heads.
    loyaltyAbility(-7) {
        target = Targets.Opponent
        effect = Effects.Composite(
            listOf(
                Effects.FlipCoins(5, storeHeadsAs = "heads"),
                Effects.SkipNextTurn(
                    target = EffectTarget.ContextTarget(0),
                    count = DynamicAmount.VariableReference("heads"),
                ),
            )
        )
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "97"
        artist = "Billy Christian"
        imageUri = "https://cards.scryfall.io/normal/front/8/f/8fbad757-4081-42f7-a460-68ac03e77510.jpg?1775937587"
    }
}
