package com.wingedsheep.mtg.sets.definitions.inv.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.ProtectionScope

/**
 * Llanowar Knight
 * {G}{W}
 * Creature — Elf Knight
 * 2/2
 * Protection from black
 */
val LlanowarKnight = card("Llanowar Knight") {
    manaCost = "{G}{W}"
    colorIdentity = "GW"
    typeLine = "Creature — Elf Knight"
    power = 2
    toughness = 2
    oracleText = "Protection from black"

    keywordAbility(KeywordAbility.Protection(ProtectionScope.Color(Color.BLACK)))

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "254"
        artist = "Heather Hudson"
        flavorText = "Her armor and steed were borrowed, but her courage was hers alone."
        imageUri = "https://cards.scryfall.io/normal/front/e/6/e6c75d89-e432-49aa-a407-555b223b7eff.jpg?1562941356"
    }
}
