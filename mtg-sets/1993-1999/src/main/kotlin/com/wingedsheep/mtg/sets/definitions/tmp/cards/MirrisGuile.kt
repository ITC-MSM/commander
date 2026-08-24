package com.wingedsheep.mtg.sets.definitions.tmp.cards

import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Mirri's Guile
 * {G}
 * Enchantment
 * At the beginning of your upkeep, you may look at the top three cards of your library, then put them back in any order.
 */
val MirrisGuile = card("Mirri's Guile") {
    manaCost = "{G}"
    colorIdentity = "G"
    typeLine = "Enchantment"
    oracleText = "At the beginning of your upkeep, you may look at the top three cards of your library, then put them back in any order."

    triggeredAbility {
        trigger = Triggers.YourUpkeep
        optional = true
        effect = Patterns.Library.lookAtTopAndReorder(3)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "236"
        artist = "Brom"
        flavorText = "Hanna was astounded. Mirri read every leaf and wisp of breeze like a book of ancient lore."
        imageUri = "https://cards.scryfall.io/normal/front/7/3/73d51a3c-95c0-4810-b847-4b8afd12fd64.jpg"
    }
}
