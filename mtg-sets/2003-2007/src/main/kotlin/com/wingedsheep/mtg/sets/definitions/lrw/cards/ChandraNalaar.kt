package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.DynamicAmount

// Current Oracle targets players or planeswalkers directly for +1 and −8.
val ChandraNalaar = card("Chandra Nalaar") {
    manaCost = "{3}{R}{R}"
    colorIdentity = "R"
    typeLine = "Legendary Planeswalker — Chandra"
    startingLoyalty = 6
    oracleText = "+1: Chandra Nalaar deals 1 damage to target player or planeswalker.\n−X: Chandra Nalaar deals X damage to target creature.\n−8: Chandra Nalaar deals 10 damage to target player or planeswalker and each creature that player or that planeswalker's controller controls."

    loyaltyAbility(+1) {
        val recipient = target("target player or planeswalker", Targets.PlayerOrPlaneswalker)
        effect = Effects.DealDamage(1, recipient)
    }

    loyaltyAbilityX {
        val creature = target("target creature", Targets.Creature)
        effect = Effects.DealDamage(DynamicAmount.XValue, creature)
    }

    loyaltyAbility(-8) {
        val recipient = target("target player or planeswalker", Targets.PlayerOrPlaneswalker)
        // The target is either the player themself or a permanent they control. Only that
        // recipient is targeted; their creatures are affected even if they have shroud.
        // An animated planeswalker in both groups is still dealt ten damage only once.
        val creatures = GameObjectFilter.Creature.targetPlayerControls(recipient) or
            GameObjectFilter.Creature.targetPlayerControls(EffectTarget.TargetController)
        effect = Effects.DealDamage(10, recipient) then
            Patterns.Group.dealDamageToAll(10, GroupFilter(creatures, excludeTarget = true))
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "159"
        artist = "Aleksi Briclot"
        imageUri = "https://cards.scryfall.io/normal/front/3/1/317e7ab7-7639-4877-aae2-3746563f2ec9.jpg?1783942878"
        ruling("2007-10-01", "To activate the second ability, you choose a value of X equal to or less than the number of loyalty counters on Chandra Nalaar. You may choose 0. You can’t choose a negative number.")
    }
}
