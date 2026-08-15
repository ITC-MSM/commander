package com.wingedsheep.mtg.sets.definitions.eoe.cards

import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.effects.DealDamageEffect
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.ContextPropertyKey
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Pain for All
 * {2}{R}
 * Enchantment — Aura
 *
 * Enchant creature you control
 * When this Aura enters, enchanted creature deals damage equal to its power to any other target.
 * Whenever enchanted creature is dealt damage, it deals that much damage to each opponent.
 */
val PainForAll = card("Pain for All") {
    manaCost = "{2}{R}"
    colorIdentity = "R"
    typeLine = "Enchantment — Aura"
    oracleText = "Enchant creature you control\n" +
        "When this Aura enters, enchanted creature deals damage equal to its power to any other target.\n" +
        "Whenever enchanted creature is dealt damage, it deals that much damage to each opponent."

    auraTarget = Targets.CreatureYouControl

    // ETB: enchanted creature deals damage equal to its power to any other target.
    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val victim = target("any other target", Targets.AnyOtherThanEnchantedCreature)
        effect = DealDamageEffect(
            amount = DynamicAmounts.enchantedCreaturePower(),
            target = victim,
            damageSource = EffectTarget.EnchantedCreature
        )
    }

    // Whenever enchanted creature is dealt damage, it deals that much damage to each opponent.
    triggeredAbility {
        trigger = Triggers.takesDamage(binding = TriggerBinding.ATTACHED)
        effect = DealDamageEffect(
            amount = DynamicAmount.ContextProperty(ContextPropertyKey.TRIGGER_DAMAGE_AMOUNT),
            target = EffectTarget.PlayerRef(Player.EachOpponent),
            damageSource = EffectTarget.EnchantedCreature
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "151"
        artist = "Dmitry Burmak"
        flavorText = "The agony that consumed him also gave him clarity of purpose."
        imageUri = "https://cards.scryfall.io/normal/front/d/2/d2948913-817b-4715-92d5-ed3cde347be7.jpg?1752947164"
        ruling("2025-07-25", "If lethal damage is dealt to the enchanted creature, Pain for All's last ability still triggers.")
        ruling("2025-07-25", "If the enchanted creature leaves the battlefield before Pain for All's second ability resolves, use its power as it last existed on the battlefield to determine how much damage is dealt.")
        ruling("2025-07-25", "If the enchanted creature is dealt damage by multiple sources at once, such as by two creatures blocking it, Pain for All's last ability triggers only once. When that triggered ability resolves, the enchanted creature deals damage equal to the total amount of damage dealt to it to each opponent.")
    }
}
