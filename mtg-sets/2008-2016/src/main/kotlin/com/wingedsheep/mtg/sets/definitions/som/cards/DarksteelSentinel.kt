package com.wingedsheep.mtg.sets.definitions.som.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Darksteel Sentinel
 * {6}
 * Artifact Creature — Golem
 * 3/3
 *
 * Flash (You may cast this spell any time you could cast an instant.)
 * Vigilance
 * Indestructible (Damage and effects that say "destroy" don't destroy this creature. If its toughness is 0 or less, it's still put into its owner's graveyard.)
 */
val DarksteelSentinel = card("Darksteel Sentinel") {
    manaCost = "{6}"
    colorIdentity = ""
    typeLine = "Artifact Creature — Golem"
    power = 3
    toughness = 3
    oracleText = "Flash (You may cast this spell any time you could cast an instant.)\n" +
        "Vigilance\n" +
        "Indestructible (Damage and effects that say \"destroy\" don't destroy this creature. If its toughness is 0 or less, it's still put into its owner's graveyard.)"

    keywords(Keyword.FLASH, Keyword.VIGILANCE, Keyword.INDESTRUCTIBLE)

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "152"
        artist = "Erica Yang"
        imageUri = "https://cards.scryfall.io/normal/front/7/6/768e9dde-59e5-4b50-9b38-b46e2a593107.jpg?1783941710"
    }
}
