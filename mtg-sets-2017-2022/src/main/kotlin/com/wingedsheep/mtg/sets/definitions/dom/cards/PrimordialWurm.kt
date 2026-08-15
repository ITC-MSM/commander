package com.wingedsheep.mtg.sets.definitions.dom.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Primordial Wurm
 * {4}{G}{G}
 * Creature — Wurm
 * 7/6
 */
val PrimordialWurm = card("Primordial Wurm") {
    manaCost = "{4}{G}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Wurm"
    power = 7
    toughness = 6

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "177"
        artist = "Yeong-Hao Han"
        flavorText = "\"You can hear its tail thrashing from a mile away. Keep in mind that its jaws may already be half a mile closer.\" —Jenson Carthalion, Yavimaya exile"
        imageUri = "https://cards.scryfall.io/normal/front/3/9/39fb52be-0e17-437d-8583-af69cdfff87b.jpg?1562734187"
    }
}
