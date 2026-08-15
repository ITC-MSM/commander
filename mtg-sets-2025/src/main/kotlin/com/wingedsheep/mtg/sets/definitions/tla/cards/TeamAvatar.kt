package com.wingedsheep.mtg.sets.definitions.tla.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.events.AttackPredicate
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Team Avatar
 * {2}{W}
 * Enchantment
 *
 * Whenever a creature you control attacks alone, it gets +X/+X until end of turn,
 * where X is the number of creatures you control.
 * {2}{W}, Discard this card: It deals damage equal to the number of creatures you
 * control to target creature.
 */
val TeamAvatar = card("Team Avatar") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Enchantment"
    oracleText = "Whenever a creature you control attacks alone, it gets +X/+X until end of turn, " +
        "where X is the number of creatures you control.\n" +
        "{2}{W}, Discard this card: It deals damage equal to the number of creatures you control to target creature."

    // Whenever a creature you control attacks alone, it gets +X/+X until end of turn,
    // where X is the number of creatures you control (counted on resolution).
    triggeredAbility {
        trigger = Triggers.attacks(
            filter = GameObjectFilter.Creature.youControl(),
            requires = setOf(AttackPredicate.Alone),
            binding = TriggerBinding.ANY,
        )
        val creatureCount = DynamicAmounts.creaturesYouControl()
        effect = Effects.ModifyStats(creatureCount, creatureCount, EffectTarget.TriggeringEntity)
    }

    // {2}{W}, Discard this card (from hand): deal damage equal to the number of creatures
    // you control to target creature.
    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{2}{W}"), Costs.DiscardSelf)
        val t = target("target", Targets.Creature)
        effect = Effects.DealDamage(DynamicAmounts.creaturesYouControl(), t)
        activateFromZone = Zone.HAND
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "38"
        artist = "Bun Toujo"
        flavorText = "Despite Sokka's best efforts, neither Boomeraang Squad nor Fearsome Foursome caught on."
        imageUri = "https://cards.scryfall.io/normal/front/a/9/a9a5f6f7-f04d-477f-b67f-fd01a5dcc0f5.jpg?1764120147"
    }
}
