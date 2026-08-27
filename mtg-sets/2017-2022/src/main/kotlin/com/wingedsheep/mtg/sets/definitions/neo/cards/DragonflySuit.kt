package com.wingedsheep.mtg.sets.definitions.neo.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Dragonfly Suit — Kamigawa: Neon Dynasty #9 (canonical printing)
 * {2}{W} · Artifact — Vehicle · 3/2
 *
 * Flying
 * Crew 1
 *
 * A *coloured* Vehicle — NEO's mechs carry mana costs with pips, so the colour identity is white
 * even though the type line is Artifact.
 */
val DragonflySuit = card("Dragonfly Suit") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Artifact — Vehicle"
    power = 3
    toughness = 2
    oracleText = "Flying\nCrew 1 (Tap any number of creatures you control with total power 1 or " +
        "more: This Vehicle becomes an artifact creature until end of turn.)"

    keywords(Keyword.FLYING)
    keywordAbility(KeywordAbility.crew(1))

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "9"
        artist = "Darren Tan"
        flavorText = "Cutting-edge Imperial sentinel mechs patrol the skies over Eiganjo."
        imageUri = "https://cards.scryfall.io/normal/front/4/9/4900862f-90f4-450b-a775-219da4ce67ef.jpg?1783923925"
    }
}
