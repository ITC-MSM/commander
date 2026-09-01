package com.wingedsheep.mtg.sets.definitions.chk.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Soratami Rainshaper
 * {2}{U}
 * Creature — Moonfolk Wizard
 * 2 / 1
 *
 * Flying
 * {3}, Return a land you control to its owner's hand: Target creature you control gains shroud until
 * end of turn. (It can't be the target of spells or abilities.)
 *
 * The Moonfolk land-bounce cost is [Costs.ReturnToHand] over `GameObjectFilter.Land` composed with
 * the mana half; "you control" on the *cost* is the atom's own enumerator, while "you control" on
 * the *target* is a controller predicate on the target filter — two different you-control clauses
 * that must not be conflated. Shroud is a real [Keyword], so the grant is the keyword overload of
 * [Effects.GrantKeyword] at its default `Duration.EndOfTurn`.
 */
val SoratamiRainshaper = card("Soratami Rainshaper") {
    manaCost = "{2}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Moonfolk Wizard"
    power = 2
    toughness = 1
    oracleText = "Flying\n" +
        "{3}, Return a land you control to its owner's hand: Target creature you control gains " +
        "shroud until end of turn. (It can't be the target of spells or abilities.)"

    keywords(Keyword.FLYING)

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{3}"), Costs.ReturnToHand(GameObjectFilter.Land))
        val t = target("target", Targets.CreatureYouControl)
        effect = Effects.GrantKeyword(Keyword.SHROUD, t)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "89"
        artist = "Ittoku"
        imageUri = "https://cards.scryfall.io/normal/front/b/9/b942ee46-c78b-414a-9de7-4376370532fa.jpg?1783944320"
    }
}
