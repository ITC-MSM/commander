package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

val SamutHazoretsChampion = card("Samut, Hazoret's Champion") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Legendary Creature — Human Warrior Cleric"
    oracleText = "Creatures you control have haste."
    power = 2
    toughness = 2

    staticAbility {
        ability = GrantKeyword(Keyword.HASTE, GroupFilter.AllCreaturesYouControl)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "251"
        artist = "Grzegorz Rutkowski"
        flavorText = "Samut doesn't pretend to know the best path, but she does know that Amonkhet will only thrive if its people keep moving forward."
        imageUri = "https://cards.scryfall.io/normal/front/b/a/ba920f23-f05c-410e-8516-c93abedf1d4d.jpg?1789729662"
        inBooster = false
    }
}
