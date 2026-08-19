package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.ProtectionScope

/**
 * Warren-Scourge Elf
 * {1}{G}
 * Creature — Elf Warrior
 * 1/1
 * Protection from Goblins
 */
val WarrenScourgeElf = card("Warren-Scourge Elf") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Elf Warrior"
    power = 1
    toughness = 1
    oracleText = "Protection from Goblins"

    keywordAbility(KeywordAbility.Protection(ProtectionScope.Subtype("Goblin")))

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "241"
        artist = "Christopher Moeller"
        flavorText = "\"If one can associate any virtue with the eyeblights, it is the talent some achieve in disposing of them. I have seen the slaying of boggarts raised nearly to an art form.\"\n—Fiala, Gilt-Leaf winnower"
        imageUri = "https://cards.scryfall.io/normal/front/7/a/7ab5fe17-5499-427b-9267-3b3a9676a087.jpg?1783942856"
    }
}
