package com.wingedsheep.mtg.sets.definitions.m14.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Regathan Firecat
 * {2}{R}
 * Creature — Elemental Cat
 * 4/1
 *
 * Vanilla — no rules text.
 */
val RegathanFirecat = card("Regathan Firecat") {
    manaCost = "{2}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Elemental Cat"
    power = 4
    toughness = 1

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "150"
        artist = "Eric Velhagen"
        flavorText = "It stalks the Regathan highlands, leaving behind melted snow, scorched earth, and the charred corpses of would-be temple robbers."
        imageUri = "https://cards.scryfall.io/normal/front/4/b/4b4df1dd-886d-4fe7-b3f7-2dca044de41c.jpg?1783939911"
    }
}
