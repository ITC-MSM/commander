package com.wingedsheep.mtg.sets.definitions.avr.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Hound of Griselbrand
 * {2}{R}{R}
 * Creature — Elemental Dog
 * 2 / 2
 *
 * Double strike
 * Undying (When this creature dies, if it had no +1/+1 counters on it, return it to the battlefield under its owner's control with a +1/+1 counter on it.)
 *
 * Both lines are printed [Keyword]s — a vanilla body with no script at all.
 */
val HoundOfGriselbrand = card("Hound of Griselbrand") {
    manaCost = "{2}{R}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Elemental Dog"
    power = 2
    toughness = 2
    oracleText = "Double strike\n" +
        "Undying (When this creature dies, if it had no +1/+1 counters on it, return it to the " +
        "battlefield under its owner's control with a +1/+1 counter on it.)"

    keywords(Keyword.DOUBLE_STRIKE, Keyword.UNDYING)

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "141"
        artist = "Svetlin Velinov"
        imageUri = "https://cards.scryfall.io/normal/front/0/f/0fe68bce-6207-4fd1-9e82-a18fd2d6ddca.jpg?1783940682"
    }
}
