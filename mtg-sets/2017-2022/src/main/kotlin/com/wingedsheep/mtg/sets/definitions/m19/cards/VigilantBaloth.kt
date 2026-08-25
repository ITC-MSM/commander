package com.wingedsheep.mtg.sets.definitions.m19.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Vigilant Baloth
 * {3}{G}{G}
 * Creature — Beast
 * 5/5
 * Vigilance (Attacking doesn't cause this creature to tap.)
 */
val VigilantBaloth = card("Vigilant Baloth") {
    manaCost = "{3}{G}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Beast"
    power = 5
    toughness = 5
    oracleText = "Vigilance (Attacking doesn't cause this creature to tap.)"

    keywords(Keyword.VIGILANCE)

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "206"
        artist = "Uriah Voth"
        flavorText = "Villagers employ watchdogs as guardians and companions. Druids prefer something a little bigger."
        imageUri = "https://cards.scryfall.io/normal/front/3/4/34ad8e5d-0c26-4588-8161-b22197715d63.jpg"
    }
}
