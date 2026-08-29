package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.events.DamageType
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.ContextPropertyKey
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Sunhome Enforcer
 * {2}{R}{W}
 * Creature — Giant Soldier
 * 2/4
 * Whenever this creature deals combat damage, you gain that much life.
 * {1}{R}: This creature gets +1/+0 until end of turn.
 *
 * "That much" is the damage the trigger fired on, read live off the event as
 * [ContextPropertyKey.TRIGGER_DAMAGE_AMOUNT]. The trigger names no recipient — combat damage to a
 * player *or* to a blocking creature both count — so it takes the bare [Triggers.dealsDamage]
 * factory rather than one of the recipient-scoped constants.
 */
val SunhomeEnforcer = card("Sunhome Enforcer") {
    manaCost = "{2}{R}{W}"
    colorIdentity = "WR"
    typeLine = "Creature — Giant Soldier"
    oracleText = "Whenever this creature deals combat damage, you gain that much life.\n" +
        "{1}{R}: This creature gets +1/+0 until end of turn."
    power = 2
    toughness = 4

    triggeredAbility {
        trigger = Triggers.dealsDamage(DamageType.Combat)
        effect = Effects.GainLife(DynamicAmount.ContextProperty(ContextPropertyKey.TRIGGER_DAMAGE_AMOUNT))
    }

    activatedAbility {
        cost = Costs.Mana("{1}{R}")
        effect = Effects.ModifyStats(1, 0, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "233"
        artist = "Greg Staples"
        flavorText = "\"Law soothed his savage heart. Where fury once burned, the enduring flame of order now shines.\"\n—Razia"
        imageUri = "https://cards.scryfall.io/normal/front/c/4/c4d5a88e-fc8f-4dd6-a1db-8e44b74ee0a1.jpg"
    }
}
