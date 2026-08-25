package com.wingedsheep.mtg.sets.definitions.kld.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Maulfist Squad
 * {3}{B}
 * Creature — Human Artificer
 * 3/1
 * Menace
 * Fabricate 1
 *
 * Both halves are printed keywords. [KeywordAbility.fabricate] is the whole of the second line —
 * the engine derives the "put a +1/+1 counter on it or create that many Servo tokens" enters
 * trigger from the keyword ability, so nothing is hand-expanded here.
 */
val MaulfistSquad = card("Maulfist Squad") {
    manaCost = "{3}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Human Artificer"
    oracleText = "Menace\n" +
        "Fabricate 1 (When this creature enters, put a +1/+1 counter on it or create a 1/1 colorless Servo artifact creature token.)"
    power = 3
    toughness = 1

    keywords(Keyword.MENACE)

    keywordAbility(KeywordAbility.fabricate(1))

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "91"
        artist = "Matt Stewart"
        flavorText = "\"Nice invention there. Looks real fragile.\""
        imageUri = "https://cards.scryfall.io/normal/front/5/e/5e4cd13a-66b6-4c65-a5a0-82f93145d16a.jpg?1783937204"
    }
}
