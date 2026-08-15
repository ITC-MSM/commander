package com.wingedsheep.mtg.sets.definitions.inv.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Smoldering Tar
 * {2}{B}{R}
 * Enchantment
 * At the beginning of your upkeep, target player loses 1 life.
 * Sacrifice this enchantment: It deals 4 damage to target creature. Activate only as a sorcery.
 */
val SmolderingTar = card("Smoldering Tar") {
    manaCost = "{2}{B}{R}"
    colorIdentity = "BR"
    typeLine = "Enchantment"
    oracleText = "At the beginning of your upkeep, target player loses 1 life.\n" +
        "Sacrifice this enchantment: It deals 4 damage to target creature. Activate only as a sorcery."

    triggeredAbility {
        trigger = Triggers.YourUpkeep
        val player = target("target player", Targets.Player)
        effect = Effects.LoseLife(1, EffectTarget.ContextTarget(0))
    }

    activatedAbility {
        cost = Costs.SacrificeSelf
        timing = TimingRule.SorcerySpeed
        val creature = target("target creature", Targets.Creature)
        effect = Effects.DealDamage(4, EffectTarget.ContextTarget(0))
        description = "Sacrifice this enchantment: It deals 4 damage to target creature. Activate only as a sorcery."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "275"
        artist = "David Day"
        imageUri = "https://cards.scryfall.io/normal/front/f/c/fcdc55c0-c8ac-49d5-969b-9bf0ee8e696c.jpg?1562946036"
    }
}
