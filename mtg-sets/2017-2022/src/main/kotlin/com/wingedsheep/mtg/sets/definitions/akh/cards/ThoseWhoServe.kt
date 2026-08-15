package com.wingedsheep.mtg.sets.definitions.akh.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Those Who Serve
 * {2}{W}
 * Creature — Zombie
 * 2/4
 *
 * Vanilla — no rules text.
 */
val ThoseWhoServe = card("Those Who Serve") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Zombie"
    power = 2
    toughness = 4

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "32"
        artist = "Volkan Baǵa"
        flavorText = "\"The dead perform all the work here—farming, building, teaching, even embalming their fellow mummies. The living need do nothing but train. What system could be more perfect?\"\n—Temmet, vizier of Naktamun"
        imageUri = "https://cards.scryfall.io/normal/front/b/4/b4f27dd9-7ee4-4cdc-8f65-a4349b6aa47f.jpg?1783936530"
    }
}
