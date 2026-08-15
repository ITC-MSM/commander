package com.wingedsheep.mtg.sets.definitions.spm.cards

import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Wisecrack
 * {2}{R}
 * Instant
 * Target creature deals damage equal to its power to itself. If that creature is attacking,
 * Wisecrack deals 2 damage to that creature's controller.
 *
 * The self-damage is dealt by the targeted creature (it is the damage source, so its power is
 * read while still on the battlefield). The 2 damage to the controller is a resolution-time
 * conditional gated on the creature still attacking.
 */
val Wisecrack = card("Wisecrack") {
    manaCost = "{2}{R}"
    colorIdentity = "R"
    typeLine = "Instant"
    oracleText = "Target creature deals damage equal to its power to itself. If that creature is attacking, " +
        "Wisecrack deals 2 damage to that creature's controller."

    spell {
        val creature = target("target creature", TargetCreature(filter = TargetFilter.Creature))
        effect = Effects.DealDamage(
            DynamicAmounts.targetPower(0),
            creature,
            damageSource = creature,
        ).then(
            ConditionalEffect(
                condition = Conditions.TargetMatchesFilter(GameObjectFilter.Creature.attacking()),
                effect = Effects.DealDamage(2, EffectTarget.TargetController),
            ),
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "98"
        artist = "Wayne Reynolds"
        flavorText = "\"I support your right to bear arms, Grizzly, but this is getting ridiculous.\""
        imageUri = "https://cards.scryfall.io/normal/front/8/f/8f452dac-bf22-4010-8a10-3c1cfa7d4df6.jpg?1757377408"
    }
}
