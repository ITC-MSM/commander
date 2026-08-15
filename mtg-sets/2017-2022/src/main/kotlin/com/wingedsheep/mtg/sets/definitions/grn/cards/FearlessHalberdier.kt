package com.wingedsheep.mtg.sets.definitions.grn.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Fearless Halberdier
 * {2}{R}
 * Creature — Human Warrior
 * 3/2
 *
 * Vanilla — no rules text.
 */
val FearlessHalberdier = card("Fearless Halberdier") {
    manaCost = "{2}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Human Warrior"
    power = 3
    toughness = 2

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "100"
        artist = "Suzanne Helmigh"
        flavorText = "\"I spent some time in the Legion, but I'm done taking orders all day.\""
        imageUri = "https://cards.scryfall.io/normal/front/3/0/30e04f16-89d0-4e75-a3cd-4dd64414050c.jpg?1783934164"
    }
}
