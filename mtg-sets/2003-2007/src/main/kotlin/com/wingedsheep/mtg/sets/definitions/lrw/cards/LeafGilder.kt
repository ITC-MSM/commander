package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Leaf Gilder
 * {1}{G}
 * Creature — Elf Druid
 * 2/1
 *
 * {T}: Add {G}.
 */
val LeafGilder = card("Leaf Gilder") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Elf Druid"
    oracleText = "{T}: Add {G}."
    power = 2
    toughness = 1

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddMana(Color.GREEN)
        manaAbility = true
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "227"
        artist = "Quinton Hoover"
        flavorText = "Eidren, perfect of Lys Alana, ordered hundreds of trees uprooted and rearranged into a pattern he deemed beautiful. Thus the Gilt-Leaf Wood was born."
        imageUri = "https://cards.scryfall.io/normal/front/3/a/3aaff934-cc79-4a01-a4be-3c4936605e4e.jpg?1783942859"
    }
}
