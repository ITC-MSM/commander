package com.wingedsheep.mtg.sets.definitions.isd.cards

import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.ForceSacrificeEffect
import com.wingedsheep.sdk.scripting.targets.TargetOpponent

/**
 * Tribute to Hunger
 * {2}{B}
 * Instant
 *
 * Target opponent sacrifices a creature of their choice. You gain life equal to that creature's toughness.
 *
 * The edict records the sacrificed permanent's last-known snapshot in the effect context, so the
 * downstream [Effects.GainLife] reads [DynamicAmounts.sacrificedToughness] for the creature the
 * opponent chose — even after it has left the battlefield. If the opponent controls no creatures,
 * nothing is sacrificed and no life is gained.
 *
 * Canonical printing lives in Innistrad (the card's earliest real printing); Foundations contributes
 * only a [com.wingedsheep.sdk.model.Printing] row.
 */
val TributeToHunger = card("Tribute to Hunger") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Instant"
    oracleText = "Target opponent sacrifices a creature of their choice. " +
        "You gain life equal to that creature's toughness."

    spell {
        val t = target("target", TargetOpponent())
        effect = Effects.Composite(
            listOf(
                ForceSacrificeEffect(GameObjectFilter.Creature, 1, t),
                Effects.GainLife(DynamicAmounts.sacrificedToughness(0))
            )
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "119"
        artist = "Dave Kendall"
        flavorText = "Marella was delighted. The ball had attracted so many suitors for her daughter, " +
            "including that handsome stranger. She wondered where the two were now . . . ."
        imageUri = "https://cards.scryfall.io/normal/front/f/7/f77e0f88-2285-4b59-9165-9948c75d77a3.jpg?1782714761"
    }
}
