package com.wingedsheep.mtg.sets.definitions.spm.cards

import com.wingedsheep.sdk.core.AbilityFlag
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Wraith, Vicious Vigilante
 * {1}{W}{U}
 * Legendary Creature — Human Detective Hero
 * 1/1
 * Double strike
 * Fear Gas — Wraith can't be blocked.
 */
val WraithViciousVigilante = card("Wraith, Vicious Vigilante") {
    manaCost = "{1}{W}{U}"
    colorIdentity = "WU"
    typeLine = "Legendary Creature — Human Detective Hero"
    oracleText = "Double strike\nFear Gas — Wraith can't be blocked."
    power = 1
    toughness = 1

    keywords(Keyword.DOUBLE_STRIKE)
    flags(AbilityFlag.CANT_BE_BLOCKED)

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "160"
        artist = "Nereida"
        flavorText = "Using tech impounded from her various arrests, Captain Yuri Watanabe took the law into her own hands."
        imageUri = "https://cards.scryfall.io/normal/front/5/f/5f46ed93-de6d-4180-9018-26ee07f75464.jpg?1757377977"
    }
}
