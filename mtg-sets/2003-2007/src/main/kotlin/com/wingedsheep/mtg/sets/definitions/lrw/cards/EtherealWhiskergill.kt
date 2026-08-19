package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CantAttackUnless

/**
 * Ethereal Whiskergill
 * {3}{U}
 * Creature — Elemental
 * 4/3
 * Flying
 * This creature can't attack unless defending player controls an Island.
 */
val EtherealWhiskergill = card("Ethereal Whiskergill") {
    manaCost = "{3}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Elemental"
    power = 4
    toughness = 3
    oracleText = "Flying\nThis creature can't attack unless defending player controls an Island."

    keywords(Keyword.FLYING)

    staticAbility {
        ability = CantAttackUnless(Conditions.DefendingPlayerControlsLandType("Island"))
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "60"
        artist = "Howard Lyon"
        flavorText = "Fallowsages debate whether the whiskergill is native to the Dark Meanders or merely dreamstuff made real."
        imageUri = "https://cards.scryfall.io/normal/front/5/b/5b9fa774-fb6d-4a2f-96d5-a449a423312e.jpg?1783942904"
    }
}
