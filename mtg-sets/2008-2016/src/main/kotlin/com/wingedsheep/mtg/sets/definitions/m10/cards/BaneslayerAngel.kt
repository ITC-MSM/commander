package com.wingedsheep.mtg.sets.definitions.m10.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.ProtectionScope

/**
 * Baneslayer Angel
 * {3}{W}{W}
 * Creature — Angel
 * 5/5
 *
 * Flying, first strike, lifelink, protection from Demons and from Dragons
 *
 * - CR 702.16g: "protection from [quality A] and from [quality B]" is shorthand for two *separate*
 *   protection abilities, so this is two [KeywordAbility.Protection] instances with a
 *   single-subtype [ProtectionScope.Subtype] each — not one scope holding a set.
 */
val BaneslayerAngel = card("Baneslayer Angel") {
    manaCost = "{3}{W}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Angel"
    power = 5
    toughness = 5
    oracleText = "Flying, first strike, lifelink, protection from Demons and from Dragons"

    keywords(Keyword.FLYING, Keyword.FIRST_STRIKE, Keyword.LIFELINK)

    keywordAbility(KeywordAbility.Protection(ProtectionScope.Subtype("Demon")))
    keywordAbility(KeywordAbility.Protection(ProtectionScope.Subtype("Dragon")))

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "4"
        artist = "Greg Staples"
        flavorText = "Some angels protect the meek and innocent. Others seek out and smite evil wherever it lurks."
        imageUri = "https://cards.scryfall.io/normal/front/6/7/6764ea7b-ccb3-4f39-b8ba-654a186210b9.jpg?1783942404"
    }
}
