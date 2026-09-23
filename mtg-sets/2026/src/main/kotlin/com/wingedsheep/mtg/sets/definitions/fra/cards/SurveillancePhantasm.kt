package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CanAttackDespiteDefender

val SurveillancePhantasm = card("Surveillance Phantasm") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Bird Illusion"
    power = 2
    toughness = 3
    oracleText = "Defender, flying, vigilance\n" +
        "As long as you've scried or surveilled this turn, this creature can attack as though it " +
        "didn't have defender.\n" +
        "{3}{U}: Surveil 1. (Look at the top card of your library. You may put it into your graveyard.)"

    keywords(Keyword.DEFENDER, Keyword.FLYING, Keyword.VIGILANCE)

    staticAbility {
        ability = CanAttackDespiteDefender(Conditions.ScriedOrSurveiledThisTurn)
    }

    activatedAbility {
        cost = Costs.Mana("{3}{U}")
        effect = Effects.Surveil(1)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "42"
        artist = "Randy Vargas"
        imageUri = "https://cards.scryfall.io/normal/front/9/d/9df8a06d-c7de-49af-8c01-06dca3dfef4b.jpg?1789385597"
        inBooster = false
    }
}
