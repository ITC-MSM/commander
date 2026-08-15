package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Axegrinder Giant
 * {4}{R}{R}
 * Creature — Giant Warrior
 * 6/4
 *
 * Vanilla — no rules text.
 */
val AxegrinderGiant = card("Axegrinder Giant") {
    manaCost = "{4}{R}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Giant Warrior"
    power = 6
    toughness = 4

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "151"
        artist = "Warren Mahy"
        flavorText = "The angriest of giants are often the most skillful weaponsmiths. Their grudges fuel endless sessions at the forge, all the while growling ferociously to themselves."
        imageUri = "https://cards.scryfall.io/normal/front/8/5/8595e9a1-010e-48a7-91e4-3d2722c8dbc0.jpg?1783942880"
    }
}
