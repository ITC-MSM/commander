package com.wingedsheep.mtg.sets.definitions.tmp.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Advance Scout
 * {1}{W}
 * Creature — Human Soldier Scout
 * 1/1
 * First strike
 * {W}: Target creature gains first strike until end of turn.
 */
val AdvanceScout = card("Advance Scout") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Soldier Scout"
    power = 1
    toughness = 1
    oracleText = "First strike\n" +
        "{W}: Target creature gains first strike until end of turn."

    keywords(Keyword.FIRST_STRIKE)

    activatedAbility {
        cost = Costs.Mana("{W}")
        val t = target("target", Targets.Creature)
        effect = Effects.GrantKeyword(Keyword.FIRST_STRIKE, t)
        description = "{W}: Target creature gains first strike until end of turn."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "1"
        artist = "Heather Hudson"
        flavorText = "\"The soldier's path is worn smooth by the tread of many feet—all in one direction, none returning.\"\n" +
            "—Oracle *en*-Vec"
        imageUri = "https://cards.scryfall.io/normal/front/8/1/81ce7e1e-ffe5-4ced-8967-9a6917245240.jpg"
    }
}
