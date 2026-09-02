package com.wingedsheep.mtg.sets.definitions.stx.cards

import com.wingedsheep.sdk.core.AbilityFlag
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Wormhole Serpent — Strixhaven: School of Mages #62 (canonical printing)
 * {4}{U} · Creature — Serpent · 3/5
 *
 * {3}{U}: Target creature can't be blocked this turn.
 *
 * "Can't be blocked" is an [AbilityFlag], not a keyword, so the ability is
 * [Effects.GrantKeyword] over [AbilityFlag.CANT_BE_BLOCKED] on the targeted creature with the
 * default until-end-of-turn duration. Nothing restricts the target's controller, so the bare
 * [Targets.Creature] requirement is correct.
 */
val WormholeSerpent = card("Wormhole Serpent") {
    manaCost = "{4}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Serpent"
    oracleText =
        "{3}{U}: Target creature can't be blocked this turn."
    power = 3
    toughness = 5

    activatedAbility {
        cost = Costs.Mana("{3}{U}")
        val creature = target("target", Targets.Creature)
        effect = Effects.GrantKeyword(AbilityFlag.CANT_BE_BLOCKED, creature)
        description = "{3}{U}: Target creature can't be blocked this turn."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "62"
        artist = "Tomasz Jedruszek"
        flavorText = "After drownings, devourings, and one highly unpleasant algae infestation, unsupervised portal use on campus was banned."
        imageUri = "https://cards.scryfall.io/normal/front/2/d/2d0fd73f-e161-4e42-b4f9-9246a9dba785.jpg?1783927371"
    }
}
