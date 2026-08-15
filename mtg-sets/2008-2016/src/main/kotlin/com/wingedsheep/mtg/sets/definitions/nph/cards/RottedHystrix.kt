package com.wingedsheep.mtg.sets.definitions.nph.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Rotted Hystrix
 * {4}{G}
 * Creature — Phyrexian Beast
 * 3/6
 *
 * Vanilla — no rules text.
 */
val RottedHystrix = card("Rotted Hystrix") {
    manaCost = "{4}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Phyrexian Beast"
    power = 3
    toughness = 6

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "120"
        artist = "Dave Allsop"
        flavorText = "Vorinclex had no grand plan. The oil did its own work, evolving creatures into worthy predators."
        imageUri = "https://cards.scryfall.io/normal/front/7/b/7bcae97d-468a-4e16-bfed-d2946f64784c.jpg?1783941299"
    }
}
