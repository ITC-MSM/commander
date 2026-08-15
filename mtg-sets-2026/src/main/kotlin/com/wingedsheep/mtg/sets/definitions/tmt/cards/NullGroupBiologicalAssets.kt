package com.wingedsheep.mtg.sets.definitions.tmt.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ConditionalStaticAbility
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Null Group Biological Assets
 * {2}{R}
 * Creature — Mutant Mercenary
 * 3/1
 *
 * During your turn, this creature has first strike.
 * Whenever this creature attacks, you may discard a card. If you do,
 * draw a card.
 */
val NullGroupBiologicalAssets = card("Null Group Biological Assets") {
    manaCost = "{2}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Mutant Mercenary"
    oracleText = "During your turn, this creature has first strike.\nWhenever this creature attacks, you may discard a card. If you do, draw a card."
    power = 3
    toughness = 1

    staticAbility {
        ability = ConditionalStaticAbility(
            ability = GrantKeyword(Keyword.FIRST_STRIKE, GroupFilter.source()),
            condition = Conditions.IsYourTurn
        )
    }

    triggeredAbility {
        trigger = Triggers.Attacks
        optional = true
        effect = Patterns.Hand.rummage(1)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "98"
        artist = "Miklós Ligeti"
        flavorText = "\"I'm a company woman. I'm part of the plan. I'm part of the team. And I enjoy my work.\"\n—Zodi"
        imageUri = "https://cards.scryfall.io/normal/front/e/b/eb44c134-fcd8-4ad8-841c-f1723ba93216.jpg?1771502666"
    }
}
