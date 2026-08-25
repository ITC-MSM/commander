package com.wingedsheep.mtg.sets.definitions.rna.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Vizkopa Vampire — Ravnica Allegiance #220
 * {2}{W/B} · Creature — Vampire · 3 / 1
 *
 * A hybrid-cost vanilla lifelinker.
 */
val VizkopaVampire = card("Vizkopa Vampire") {
    manaCost = "{2}{W/B}"
    colorIdentity = "BW"
    typeLine = "Creature — Vampire"
    power = 3
    toughness = 1
    oracleText = "Lifelink"

    keywords(Keyword.LIFELINK)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "220"
        artist = "Winona Nelson"
        flavorText = "Orzhov vampires look for allies in unlikely places in case their new guildmaster turns on them. The fate of the Obzedat is proof of Kaya's power and her hatred of the living dead."
        imageUri = "https://cards.scryfall.io/normal/front/6/1/61245466-694f-4a80-b556-4e7f876aedca.jpg"
    }
}
