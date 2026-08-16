package com.wingedsheep.mtg.sets.definitions.one.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CantBeBlockedBy
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Skyscythe Engulfer
 * {5}{G}
 * Creature — Phyrexian Beast
 * 6/5
 *
 * Reach, trample
 * This creature can't be blocked by creatures with flying.
 *
 * The evasion clause is the standard [CantBeBlockedBy] restriction; its blocker filter is matched
 * against projected keywords, so flying granted by a continuous effect counts.
 */
val SkyscytheEngulfer = card("Skyscythe Engulfer") {
    manaCost = "{5}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Phyrexian Beast"
    power = 6
    toughness = 5
    oracleText = "Reach, trample\n" +
        "This creature can't be blocked by creatures with flying."

    keywords(Keyword.REACH, Keyword.TRAMPLE)

    staticAbility {
        ability = CantBeBlockedBy(GameObjectFilter.Creature.withKeyword(Keyword.FLYING))
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "183"
        artist = "Helge C. Balzer"
        imageUri = "https://cards.scryfall.io/normal/front/d/7/d7898399-3c52-402c-9cd7-baad2cb7f00e.jpg?1783918010"
    }
}
