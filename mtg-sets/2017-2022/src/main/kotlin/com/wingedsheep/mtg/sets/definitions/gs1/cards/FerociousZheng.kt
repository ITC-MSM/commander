package com.wingedsheep.mtg.sets.definitions.gs1.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Ferocious Zheng
 * {2}{G}{G}
 * Creature — Cat Beast
 * 4/4
 *
 * Vanilla — no rules text.
 */
val FerociousZheng = card("Ferocious Zheng") {
    manaCost = "{2}{G}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Cat Beast"
    power = 4
    toughness = 4

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "28"
        artist = "Yutaka Li"
        flavorText = "Known for their glowing horn and stone-rattling roar, zheng are the fiercest predators in the forest. Few survived before Jiang Yanggu came."
        imageUri = "https://cards.scryfall.io/normal/front/7/a/7a6d1184-15e0-4b41-ba2d-4f68e91c61d4.jpg?1783934626"
    }
}
