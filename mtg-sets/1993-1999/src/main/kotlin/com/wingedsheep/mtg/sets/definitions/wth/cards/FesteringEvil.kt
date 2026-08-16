package com.wingedsheep.mtg.sets.definitions.wth.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.DealDamageEffect
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Festering Evil
 * {3}{B}{B}
 * Enchantment
 * At the beginning of your upkeep, this enchantment deals 1 damage to each creature and each player.
 * {B}{B}, Sacrifice this enchantment: It deals 3 damage to each creature and each player.
 *
 * "each creature and each player" is the standard two-part sweep: [Effects.ForEachInGroup] over
 * [GroupFilter.AllCreatures] then [Effects.ForEachPlayer] over [Player.Each].
 */
val FesteringEvil = card("Festering Evil") {
    manaCost = "{3}{B}{B}"
    colorIdentity = "B"
    typeLine = "Enchantment"
    oracleText = "At the beginning of your upkeep, this enchantment deals 1 damage to each creature and each player.\n{B}{B}, Sacrifice this enchantment: It deals 3 damage to each creature and each player."

    triggeredAbility {
        trigger = Triggers.YourUpkeep
        effect = Effects.ForEachInGroup(GroupFilter.AllCreatures, DealDamageEffect(1, EffectTarget.Self)) then
            Effects.ForEachPlayer(Player.Each, listOf(Effects.DealDamage(1, EffectTarget.Controller)))
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{B}{B}"), Costs.SacrificeSelf)
        effect = Effects.ForEachInGroup(GroupFilter.AllCreatures, DealDamageEffect(3, EffectTarget.Self)) then
            Effects.ForEachPlayer(Player.Each, listOf(Effects.DealDamage(3, EffectTarget.Controller)))
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "68"
        artist = "John Matson"
        imageUri = "https://cards.scryfall.io/normal/front/2/d/2d688bda-fee2-496d-9793-794c2568b54e.jpg?1783946734"
    }
}
