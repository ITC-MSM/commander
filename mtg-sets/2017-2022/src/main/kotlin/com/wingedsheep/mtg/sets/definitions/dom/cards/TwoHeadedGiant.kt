package com.wingedsheep.mtg.sets.definitions.dom.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.FlipTwoCoinsEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Two-Headed Giant
 * {2}{R}{R}
 * Creature — Giant Warrior
 * 4/4
 * Whenever this creature attacks, flip two coins. If both coins come up heads,
 * this creature gains double strike until end of turn. If both coins come up tails,
 * this creature gains menace until end of turn.
 */
val TwoHeadedGiant = card("Two-Headed Giant") {
    manaCost = "{2}{R}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Giant Warrior"
    power = 4
    toughness = 4
    oracleText = "Whenever Two-Headed Giant attacks, flip two coins. If both coins come up heads, Two-Headed Giant gains double strike until end of turn. If both coins come up tails, Two-Headed Giant gains menace until end of turn."

    triggeredAbility {
        trigger = Triggers.Attacks
        effect = FlipTwoCoinsEffect(
            bothHeadsEffect = Effects.GrantKeyword(Keyword.DOUBLE_STRIKE, EffectTarget.Self),
            bothTailsEffect = Effects.GrantKeyword(Keyword.MENACE, EffectTarget.Self)
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "147"
        artist = "Simon Dominic"
        flavorText = "He watches the pass in both directions, and all must pay his toll."
        imageUri = "https://cards.scryfall.io/normal/front/6/f/6f86c365-3ce5-45e5-b610-6f2587f99b42.jpg?1562737530"
    }
}
