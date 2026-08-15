package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.MayEffect

/**
 * Summon: G.F. Ifrit
 * {2}{R}
 * Enchantment Creature — Saga Demon
 * 3/2
 * (As this Saga enters and after your draw step, add a lore counter. Sacrifice after IV.)
 * I, II — You may discard a card. If you do, draw a card.
 * III, IV — Add {R}.
 */
val SummonGfIfrit = card("Summon: G.F. Ifrit") {
    manaCost = "{2}{R}"
    colorIdentity = "R"
    typeLine = "Enchantment Creature — Saga Demon"
    oracleText = "(As this Saga enters and after your draw step, add a lore counter. Sacrifice after IV.)\n" +
        "I, II — You may discard a card. If you do, draw a card.\n" +
        "III, IV — Add {R}."
    power = 3
    toughness = 2

    // "You may discard a card. If you do, draw a card."
    val rummage = MayEffect(
        Effects.Composite(
            Patterns.Hand.discardCards(1),
            Effects.DrawCards(1),
        ),
    )
    val addRed = Effects.AddMana(Color.RED, 1)

    sagaChapter(1) { effect = rummage }
    sagaChapter(2) { effect = rummage }
    sagaChapter(3) { effect = addRed }
    sagaChapter(4) { effect = addRed }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "163"
        artist = "Lie Setiawan"
        imageUri = "https://cards.scryfall.io/normal/front/c/6/c6c73092-5195-4bdc-b039-a699f6e297b2.jpg?1749639154"
    }
}
