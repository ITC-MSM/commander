package com.wingedsheep.mtg.sets.definitions.dmu.cards

import com.wingedsheep.sdk.core.CardType
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Argivian Phalanx
 * {5}{W}
 * Creature — Human Kor Soldier
 * 4/4
 * Affinity for creatures (This spell costs {1} less to cast for each creature you control.)
 * Vigilance (Attacking doesn't cause this creature to tap.)
 */
val ArgivianPhalanx = card("Argivian Phalanx") {
    manaCost = "{5}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Kor Soldier"
    oracleText = "Affinity for creatures (This spell costs {1} less to cast for each creature you control.)\nVigilance (Attacking doesn't cause this creature to tap.)"
    power = 4
    toughness = 4

    keywordAbility(KeywordAbility.Affinity(CardType.CREATURE))
    keywords(Keyword.VIGILANCE)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "5"
        artist = "Josh Hass"
        flavorText = "New Argive welcomed the kor people when they were displaced by the Rathi overlay. The kor repay that kindness with fierce loyalty."
        imageUri = "https://cards.scryfall.io/normal/front/a/f/afbadc37-1b4f-4237-a3c0-772bafb18aa0.jpg?1783921373"
    }
}
