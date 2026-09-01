package com.wingedsheep.mtg.sets.definitions.chk.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ActivationRestriction
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Akki Avalanchers
 * {R}
 * Creature — Goblin Warrior
 * 1 / 1
 *
 * Sacrifice a land: This creature gets +2/+0 until end of turn. Activate only once each turn.
 *
 * The whole card is one activated ability: a bare [Costs.Sacrifice] over `GameObjectFilter.Land`
 * (no mana half at all) pumping [EffectTarget.Self], with the printed "Activate only once each
 * turn" carried by [ActivationRestriction.OncePerTurn] rather than by any condition on the effect.
 */
val AkkiAvalanchers = card("Akki Avalanchers") {
    manaCost = "{R}"
    colorIdentity = "R"
    typeLine = "Creature — Goblin Warrior"
    power = 1
    toughness = 1
    oracleText = "Sacrifice a land: This creature gets +2/+0 until end of turn. Activate only once each turn."

    activatedAbility {
        cost = Costs.Sacrifice(GameObjectFilter.Land)
        effect = Effects.ModifyStats(2, 0, EffectTarget.Self)
        restrictions = listOf(ActivationRestriction.OncePerTurn)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "151"
        artist = "Matt Thompson"
        flavorText = "Among Godo's hordes, \"beware of falling rocks\" came to mean \"akki live nearby.\""
        imageUri = "https://cards.scryfall.io/normal/front/b/f/bfef6acb-a11c-4a3f-9cfb-9394dece2675.jpg?1783944307"
    }
}
