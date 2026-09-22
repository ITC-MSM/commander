package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

val YargleGoliathOfOtaria = card("Yargle, Goliath of Otaria") {
    manaCost = "{4}{U}"
    colorIdentity = "U"
    typeLine = "Legendary Creature — Frog Spirit"
    power = 3
    toughness = 9
    oracleText = ""

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "225"
        artist = "Jehan Choo"
        flavorText = "\"Why sure I remember Yar-Kul! Man had the most refined palate in all Otaria. I still have some of his cookbooks. Shame he went and trespassed in Krosan forest . . . There ain't a single soul who comes outta those woods unchanged.\"\n—Mister Foundforks, culinarian"
        imageUri = "https://cards.scryfall.io/normal/front/f/4/f45ba926-6496-4bd4-96eb-663946d56bbf.jpg?1789128043"
        inBooster = false
    }
}
