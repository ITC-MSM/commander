package com.wingedsheep.mtg.sets.definitions.otj.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ConditionalStaticAbility
import com.wingedsheep.sdk.scripting.GrantKeyword

/**
 * Stoic Sphinx
 * {2}{U}{U}
 * Creature — Sphinx
 * 5/3
 *
 * Flash
 * Flying
 * This creature has hexproof as long as you haven't cast a spell this turn.
 *
 * The hexproof clause is a conditional static ability (CR 604.3): it grants hexproof while the
 * controller's spell count for the turn is zero, dropping off the moment they cast a spell.
 */
val StoicSphinx = card("Stoic Sphinx") {
    manaCost = "{2}{U}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Sphinx"
    power = 5
    toughness = 3
    oracleText = "Flash\nFlying\nThis creature has hexproof as long as you haven't cast a spell this turn."

    keywords(Keyword.FLASH, Keyword.FLYING)

    staticAbility {
        ability = ConditionalStaticAbility(
            ability = GrantKeyword(Keyword.HEXPROOF, Filters.Self),
            condition = Conditions.Not(Conditions.YouCastSpellsThisTurn(1)),
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "71"
        artist = "Andreas Zafiratos"
        flavorText = "Lucky travelers reported a mighty winged creature among the geysers. " +
            "Unlucky ones didn't make it back."
        imageUri = "https://cards.scryfall.io/normal/front/f/9/f93f5055-30d8-4fc4-afa5-29212e8c7536.jpg?1712355517"
    }
}
