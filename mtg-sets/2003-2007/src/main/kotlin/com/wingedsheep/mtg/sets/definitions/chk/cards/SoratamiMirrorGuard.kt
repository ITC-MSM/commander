package com.wingedsheep.mtg.sets.definitions.chk.cards

import com.wingedsheep.sdk.core.AbilityFlag
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Soratami Mirror-Guard
 * {3}{U}
 * Creature — Moonfolk Wizard
 * 3 / 1
 *
 * Flying
 * {2}, Return a land you control to its owner's hand: Target creature with power 2 or less can't be
 * blocked this turn.
 *
 * The Moonfolk land-bounce cost is [Costs.ReturnToHand] over `GameObjectFilter.Land` composed with
 * the mana half; "you control" lives in the atom's enumerator. "Can't be blocked" is an
 * [AbilityFlag], not a [Keyword], so the grant goes through the `AbilityFlag` overload of
 * [Effects.GrantKeyword] at its default `Duration.EndOfTurn` — the printed "this turn". The power
 * clamp belongs to the *target restriction*, so it rides on the target filter rather than on the
 * effect.
 */
val SoratamiMirrorGuard = card("Soratami Mirror-Guard") {
    manaCost = "{3}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Moonfolk Wizard"
    power = 3
    toughness = 1
    oracleText = "Flying\n" +
        "{2}, Return a land you control to its owner's hand: Target creature with power 2 or less can't be blocked this turn."

    keywords(Keyword.FLYING)

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{2}"), Costs.ReturnToHand(GameObjectFilter.Land))
        val t = target("target", Targets.CreatureWithPowerAtMost(2))
        effect = Effects.GrantKeyword(AbilityFlag.CANT_BE_BLOCKED, t)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "87"
        artist = "Wayne England"
        imageUri = "https://cards.scryfall.io/normal/front/3/f/3ff8968b-cd96-4f44-85f8-2af20d61d7cb.jpg?1783944321"
    }
}
