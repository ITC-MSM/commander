package com.wingedsheep.mtg.sets.definitions.ltr.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Goblin Assailant
 * {1}{R}
 * Creature — Goblin Warrior
 * 2/2
 */
val GoblinAssailant = card("Goblin Assailant") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Goblin Warrior"
    power = 2
    toughness = 2
    oracleText = ""

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "295"
        artist = "Anton Solovianchyk"
        flavorText = "The fighting waxed furious on the fields of the Pelennor; and the din of arms rose upon high, with the crying of Men and the neighing of horses. But the hosts of Mordor were enheartened, and filled with a new fury they came yelling to the onset."
        imageUri = "https://cards.scryfall.io/normal/front/b/4/b4bda198-7ab4-4c6b-9fb1-93ac54c06a85.jpg?1687424910"
    }
}
