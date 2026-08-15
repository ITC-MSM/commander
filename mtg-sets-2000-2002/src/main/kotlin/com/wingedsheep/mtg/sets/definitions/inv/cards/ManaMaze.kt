package com.wingedsheep.mtg.sets.definitions.inv.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CantCastSpellsSharingColorWithLastCast

/**
 * Mana Maze
 * {1}{U}
 * Enchantment
 * Players can't cast spells that share a color with the spell most recently cast this turn.
 */
val ManaMaze = card("Mana Maze") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Enchantment"
    oracleText = "Players can't cast spells that share a color with the spell most recently cast this turn."

    staticAbility {
        ability = CantCastSpellsSharingColorWithLastCast
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "59"
        artist = "Rebecca Guay"
        imageUri = "https://cards.scryfall.io/normal/front/0/d/0d62cc17-8fa3-495c-a098-ffbbec89fa53.jpg?1562897736"
    }
}
