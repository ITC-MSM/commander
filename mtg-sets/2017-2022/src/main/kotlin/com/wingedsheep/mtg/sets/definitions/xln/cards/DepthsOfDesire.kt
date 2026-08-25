package com.wingedsheep.mtg.sets.definitions.xln.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Depths of Desire
 * {2}{U}
 * Instant
 *
 * Return target creature to its owner's hand. Create a Treasure token.
 */
val DepthsOfDesire = card("Depths of Desire") {
    manaCost = "{2}{U}"
    colorIdentity = "U"
    typeLine = "Instant"
    oracleText = "Return target creature to its owner's hand. Create a Treasure token. " +
        "(It's an artifact with \"{T}, Sacrifice this token: Add one mana of any color.\")"

    spell {
        val victim = target("target", Targets.Creature)
        effect = Effects.ReturnToHand(victim) then Effects.CreateTreasure()
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "52"
        artist = "John Stanko"
        flavorText = "Pockets full of gold, lungs full of brine."
        imageUri = "https://cards.scryfall.io/normal/front/6/3/63420437-76a9-40cf-aedc-0ca1e73fcc0b.jpg"
    }
}
