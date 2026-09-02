package com.wingedsheep.mtg.sets.definitions.stx.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Moldering Karok — Strixhaven: School of Mages #206 (canonical printing)
 * {2}{B}{G} · Creature — Zombie Crocodile · 3/3
 *
 * Trample, lifelink
 *
 * Two plain evergreen keywords, nothing else — both are simple [Keyword] markers the engine
 * reads directly.
 */
val MolderingKarok = card("Moldering Karok") {
    manaCost = "{2}{B}{G}"
    colorIdentity = "BG"
    typeLine = "Creature — Zombie Crocodile"
    oracleText =
        "Trample, lifelink"
    power = 3
    toughness = 3

    keywords(Keyword.TRAMPLE, Keyword.LIFELINK)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "206"
        artist = "Nicholas Gregory"
        flavorText = "Necroluminescence is common among the undead creatures of Sedgemoor, giving the midnight swamp an eerie glow. It's comforting—from a distance."
        imageUri = "https://cards.scryfall.io/normal/front/7/0/70c2ef30-0db5-4ef5-999c-7ffa48769421.jpg?1783927305"
    }
}
