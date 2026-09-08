package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.champion
import com.wingedsheep.sdk.model.Rarity

/**
 * Nova Chaser
 * {3}{R}
 * Creature — Elemental Warrior
 * 10/2
 *
 * Trample
 * Champion an Elemental
 *
 * "An Elemental" is a bare tribal noun, so per CR 109.2 the champion quality is an Elemental
 * *permanent* — Lorwyn's Kindred noncreature Elementals qualify, not just creatures. The
 * [champion] `Subtype` overload builds exactly that filter.
 */
val NovaChaser = card("Nova Chaser") {
    manaCost = "{3}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Elemental Warrior"
    power = 10
    toughness = 2
    oracleText = "Trample\n" +
        "Champion an Elemental (When this enters, sacrifice it unless you exile another Elemental " +
        "you control. When this leaves the battlefield, that card returns to the battlefield.)"

    keywords(Keyword.TRAMPLE)
    champion(Subtype.ELEMENTAL)

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "187"
        artist = "Dan Murayama Scott"
        imageUri = "https://cards.scryfall.io/normal/front/0/7/07f97507-abbe-4c8b-9683-f044e38f8d4b.jpg?1783942870"
    }
}
