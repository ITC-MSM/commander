package com.wingedsheep.mtg.sets.definitions.ice.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Pygmy Allosaurus
 * {2}{G}
 * Creature — Dinosaur
 * 2/2
 *
 * Swampwalk (This creature can't be blocked as long as defending player controls a Swamp.)
 *
 * Swampwalk alone.
 */
val PygmyAllosaurus = card("Pygmy Allosaurus") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Dinosaur"
    power = 2
    toughness = 2
    oracleText = "Swampwalk (This creature can't be blocked as long as defending player controls a Swamp.)"

    keywords(Keyword.SWAMPWALK)

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "257"
        artist = "Anson Maddocks"
        flavorText = "\"I don't understand the appeal of keeping these things as pets, unless you want your children eaten.\"\n—General Jarkeld, the Arctic Fox"
        imageUri = "https://cards.scryfall.io/normal/front/8/8/88a68767-9822-4f15-895e-32164e2159be.jpg"
    }
}
