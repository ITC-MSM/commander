package com.wingedsheep.mtg.sets.definitions.neo.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Futurist Sentinel — Kamigawa: Neon Dynasty #54 (canonical printing)
 * {3}{U} · Artifact — Vehicle · 6/6
 *
 * Crew 3
 */
val FuturistSentinel = card("Futurist Sentinel") {
    manaCost = "{3}{U}"
    colorIdentity = "U"
    typeLine = "Artifact — Vehicle"
    power = 6
    toughness = 6
    oracleText = "Crew 3 (Tap any number of creatures you control with total power 3 or more: " +
        "This Vehicle becomes an artifact creature until end of turn.)"

    keywordAbility(KeywordAbility.crew(3))

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "54"
        artist = "Daniel Ljunggren"
        flavorText = "\"I folded a model in paper first to test the relative tensile strength of " +
            "the plates.\"\n—Katsumasa, the Animator"
        imageUri = "https://cards.scryfall.io/normal/front/8/6/8672f626-3e46-447b-841f-6a04cd380653.jpg?1783923903"
    }
}
