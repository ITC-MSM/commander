package com.wingedsheep.mtg.sets.definitions.thb.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Vexing Gull
 * {2}{U}
 * Creature — Bird
 * 2/2
 *
 * Flash
 * Flying
 *
 * Two printed keywords, nothing else — a flash flier that ambushes an attacker for three mana.
 */
val VexingGull = card("Vexing Gull") {
    manaCost = "{2}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Bird"
    power = 2
    toughness = 2
    oracleText = "Flash\n" +
        "Flying"

    keywords(Keyword.FLASH, Keyword.FLYING)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "79"
        artist = "Volkan Baǵa"
        flavorText = "\"May the skies be clear of gales and gulls.\"\n—Meletian prayer"
        imageUri = "https://cards.scryfall.io/normal/front/8/6/869fee23-df75-448d-9fca-6ba6713d459f.jpg"
    }
}
