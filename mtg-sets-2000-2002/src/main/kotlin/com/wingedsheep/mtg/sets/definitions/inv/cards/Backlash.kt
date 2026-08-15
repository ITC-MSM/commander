package com.wingedsheep.mtg.sets.definitions.inv.cards

import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Backlash
 * {1}{B}{R}
 * Instant
 *
 * Tap target untapped creature. That creature deals damage equal to its power to its controller.
 *
 * The tapped creature is the source of the damage (so its power, including continuous effects and
 * counters, is read while it is still on the battlefield).
 */
val Backlash = card("Backlash") {
    manaCost = "{1}{B}{R}"
    colorIdentity = "BR"
    typeLine = "Instant"
    oracleText = "Tap target untapped creature. That creature deals damage equal to its power to its controller."

    spell {
        val creature = target("target untapped creature", TargetCreature(filter = TargetFilter.UntappedCreature))
        effect = Effects.Tap(creature) then
            Effects.DealDamage(
                DynamicAmounts.targetPower(0),
                EffectTarget.TargetController,
                damageSource = creature
            )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "234"
        artist = "Chippy"
        flavorText = "Darigaaz decided his foe would be more useful as a weapon."
        imageUri = "https://cards.scryfall.io/normal/front/d/a/dadf030d-5451-43fc-bf0c-c1629fdf88ec.jpg?1562938984"
    }
}
