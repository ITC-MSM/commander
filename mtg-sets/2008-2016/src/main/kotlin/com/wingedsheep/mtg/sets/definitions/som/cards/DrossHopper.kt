package com.wingedsheep.mtg.sets.definitions.som.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Dross Hopper — Scars of Mirrodin #60
 * {1}{B} · Creature — Phyrexian Insect Horror · 2 / 1
 *
 * Sacrifice a creature: This creature gains flying until end of turn.
 *
 * The sacrifice filter is unrestricted — the Hopper is itself a legal creature to sacrifice, which
 * fizzles the ability for want of a source but is a legal activation (CR 601.2h / 602.2a).
 */
val DrossHopper = card("Dross Hopper") {
    manaCost = "{1}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Phyrexian Insect Horror"
    power = 2
    toughness = 1
    oracleText = "Sacrifice a creature: This creature gains flying until end of turn."

    activatedAbility {
        cost = Costs.Sacrifice(GameObjectFilter.Creature)
        effect = Effects.GrantKeyword(Keyword.FLYING, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "60"
        artist = "Dave Allsop"
        flavorText = "Bred in the vicious Mephidross, dross hoppers learned to eat quickly and escape faster."
        imageUri = "https://cards.scryfall.io/normal/front/1/a/1a0656f6-a016-479a-a003-72e106e986b0.jpg?1783941732"
    }
}
