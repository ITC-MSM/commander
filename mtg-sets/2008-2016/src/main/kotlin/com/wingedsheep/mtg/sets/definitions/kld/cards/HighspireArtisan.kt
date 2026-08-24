package com.wingedsheep.mtg.sets.definitions.kld.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Highspire Artisan
 * {2}{G}
 * Creature — Elf Artificer
 * 0/3
 * Reach (This creature can block creatures with flying.)
 * Fabricate 1 (When this creature enters, put a +1/+1 counter on it or create a 1/1 colorless
 * Servo artifact creature token.)
 *
 * Fabricate is a keyword ability (CR 702.122) — the engine derives its enters-the-battlefield
 * modal trigger from [KeywordAbility.fabricate], so the card carries nothing but the keywords.
 */
val HighspireArtisan = card("Highspire Artisan") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Elf Artificer"
    oracleText = "Reach (This creature can block creatures with flying.)\n" +
        "Fabricate 1 (When this creature enters, put a +1/+1 counter on it or create a 1/1 colorless Servo artifact creature token.)"
    power = 0
    toughness = 3
    keywords(Keyword.REACH)

    keywordAbility(KeywordAbility.fabricate(1))

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "157"
        artist = "Anna Steinbauer"
        imageUri = "https://cards.scryfall.io/normal/front/7/2/72a26cb0-655f-4bf8-899a-952d5bfe2b42.jpg?1783937179"
    }
}
