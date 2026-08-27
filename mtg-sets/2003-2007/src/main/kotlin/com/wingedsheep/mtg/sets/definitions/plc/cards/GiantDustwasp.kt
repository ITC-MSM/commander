package com.wingedsheep.mtg.sets.definitions.plc.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Giant Dustwasp
 * {3}{G}{G}
 * Creature — Insect
 * 3/3
 * Flying
 * Suspend 4—{1}{G}
 */
val GiantDustwasp = card("Giant Dustwasp") {
    manaCost = "{3}{G}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Insect"
    power = 3
    toughness = 3
    oracleText = "Flying\n" +
        "Suspend 4—{1}{G} (Rather than cast this card from your hand, you may pay {1}{G} and exile it with four time counters on it. At the beginning of your upkeep, remove a time counter. When the last is removed, you may cast it without paying its mana cost. It has haste.)"

    keywords(Keyword.FLYING)
    keywordAbility(KeywordAbility.suspend("{1}{G}", 4))

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "129"
        artist = "Greg Hildebrandt"
        imageUri = "https://cards.scryfall.io/normal/front/1/3/13d6938b-89e6-4cf2-8372-17f1682124ec.jpg"
    }
}
