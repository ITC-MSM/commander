package com.wingedsheep.mtg.sets.definitions.roe.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Daggerback Basilisk
 * {2}{G}
 * Creature — Basilisk
 * 2/2
 * Deathtouch (Any amount of damage this deals to a creature is enough to destroy it.)
 */
val DaggerbackBasilisk = card("Daggerback Basilisk") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Basilisk"
    power = 2
    toughness = 2
    oracleText = "Deathtouch (Any amount of damage this deals to a creature is enough to destroy it.)"

    keywords(Keyword.DEATHTOUCH)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "182"
        artist = "Jesper Ejsing"
        flavorText = "\"Petrifying gaze, deadly fangs, knifelike dorsal spines, venomous saliva . . . Am I missing anything? . . . Toxic bones? Seriously?\"\n" +
            "—Samila, Murasa Expeditionary House"
        imageUri = "https://cards.scryfall.io/normal/front/d/4/d4343cb0-6490-496c-9d58-ac1f7d3a91c6.jpg"
    }
}
