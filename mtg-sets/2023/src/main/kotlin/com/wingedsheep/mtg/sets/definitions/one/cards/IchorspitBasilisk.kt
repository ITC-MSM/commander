package com.wingedsheep.mtg.sets.definitions.one.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Ichorspit Basilisk
 * {2}{G}
 * Creature — Phyrexian Basilisk
 * 1/3
 *
 * Deathtouch
 * Toxic 1 (Players dealt combat damage by this creature also get a poison counter.)
 */
val IchorspitBasilisk = card("Ichorspit Basilisk") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Phyrexian Basilisk"
    power = 1
    toughness = 3
    oracleText = "Deathtouch\n" +
        "Toxic 1 (Players dealt combat damage by this creature also get a poison counter.)"

    keywords(Keyword.DEATHTOUCH)
    keywordAbility(KeywordAbility.Numeric(Keyword.TOXIC, 1))

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "170"
        artist = "Joe Slucher"
        imageUri = "https://cards.scryfall.io/normal/front/0/9/0955b64f-6721-4fa6-b161-cae404cb5b9f.jpg?1783918014"
    }
}
