package com.wingedsheep.mtg.sets.definitions.avr.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.ProtectionScope

/**
 * Midnight Duelist
 * {W}
 * Creature — Human Soldier
 * 1 / 2
 *
 * Protection from Vampires
 *
 * [ProtectionScope.Subtype] holds a single subtype, so protection from a creature type is one
 * `keywordAbility` per type (Elite Inquisitor shape).
 */
val MidnightDuelist = card("Midnight Duelist") {
    manaCost = "{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Soldier"
    power = 1
    toughness = 2
    oracleText = "Protection from Vampires"

    keywordAbility(KeywordAbility.Protection(ProtectionScope.Subtype("Vampire")))

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "27"
        artist = "Bud Cook"
        flavorText = "Avacyn's return did not rid him of his desire for revenge."
        imageUri = "https://cards.scryfall.io/normal/front/2/3/2371bd0c-ca38-4a62-b525-bef4d1ca0646.jpg?1783940731"
    }
}
