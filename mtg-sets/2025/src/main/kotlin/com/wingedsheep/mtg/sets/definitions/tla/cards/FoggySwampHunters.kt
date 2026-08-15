package com.wingedsheep.mtg.sets.definitions.tla.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ConditionalStaticAbility
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Foggy Swamp Hunters
 * {3}{B}
 * Creature — Human Ranger Ally
 * 3/4
 *
 * As long as you've drawn two or more cards this turn, this creature has lifelink and menace.
 * (It can't be blocked except by two or more creatures.)
 *
 * Two source-scoped [GrantKeyword] static abilities (lifelink, menace), each wrapped in a
 * [ConditionalStaticAbility] gated on [Conditions.YouDrewCardsThisTurn] (threshold 2). The
 * cards-drawn-this-turn tracker is read each time projected state is computed, so both keywords
 * appear the moment you've drawn your second card and revert at end of turn.
 */
val FoggySwampHunters = card("Foggy Swamp Hunters") {
    manaCost = "{3}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Human Ranger Ally"
    power = 3
    toughness = 4
    oracleText = "As long as you've drawn two or more cards this turn, this creature has " +
        "lifelink and menace. (It can't be blocked except by two or more creatures.)"

    staticAbility {
        ability = ConditionalStaticAbility(
            ability = GrantKeyword(Keyword.LIFELINK, GroupFilter.source()),
            condition = Conditions.YouDrewCardsThisTurn(2),
        )
    }

    staticAbility {
        ability = ConditionalStaticAbility(
            ability = GrantKeyword(Keyword.MENACE, GroupFilter.source()),
            condition = Conditions.YouDrewCardsThisTurn(2),
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "101"
        artist = "Bun Toujo"
        flavorText = "\"Set my lines by the riverbed! Caught ten fish and I killed 'em dead!\"\n" +
            "—Foggy Swamp Tribe ditty"
        imageUri = "https://cards.scryfall.io/normal/front/3/f/3f6e5869-ca25-4b98-a844-8a498cb40aab.jpg?1764120698"
    }
}
