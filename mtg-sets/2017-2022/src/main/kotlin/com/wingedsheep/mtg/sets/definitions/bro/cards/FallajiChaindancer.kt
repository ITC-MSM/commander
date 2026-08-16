package com.wingedsheep.mtg.sets.definitions.bro.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Fallaji Chaindancer
 * {3}{R}
 * Creature — Human Soldier
 * 2/4
 * {2}: This creature gains double strike until end of turn.
 */
val FallajiChaindancer = card("Fallaji Chaindancer") {
    manaCost = "{3}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Human Soldier"
    power = 2
    toughness = 4
    oracleText = "{2}: This creature gains double strike until end of turn."

    activatedAbility {
        cost = Costs.Mana("{2}")
        effect = Effects.GrantKeyword(Keyword.DOUBLE_STRIKE, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "134"
        artist = "Filipe Pagliuso"
        flavorText = "She was eager to show the invading Yotians how she earned her nickname, \"The Sandstorm of Tomakul.\""
        imageUri = "https://cards.scryfall.io/normal/front/4/d/4dc2da75-160d-47f9-b978-e153262ec1fc.jpg?1783920073"
    }
}
