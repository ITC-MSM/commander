package com.wingedsheep.mtg.sets.definitions.iko.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.Mode
import com.wingedsheep.sdk.scripting.effects.ModalEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Light of Hope
 * {W}
 * Instant
 * Choose one —
 * • You gain 4 life.
 * • Destroy target enchantment.
 * • Put a +1/+1 counter on target creature.
 */
val LightOfHope = card("Light of Hope") {
    manaCost = "{W}"
    colorIdentity = "W"
    typeLine = "Instant"
    oracleText = "Choose one —\n• You gain 4 life.\n• Destroy target enchantment.\n• Put a +1/+1 counter on target creature."

    spell {
        effect = ModalEffect.chooseOne(
            Mode.noTarget(
                effect = Effects.GainLife(4),
                description = "You gain 4 life."
            ),
            Mode.withTarget(
                effect = Effects.Destroy(EffectTarget.ContextTarget(0)),
                target = Targets.Enchantment,
                description = "Destroy target enchantment."
            ),
            Mode.withTarget(
                effect = Effects.AddCounters(
                    Counters.PLUS_ONE_PLUS_ONE,
                    1,
                    EffectTarget.ContextTarget(0)
                ),
                target = Targets.Creature,
                description = "Put a +1/+1 counter on target creature."
            )
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "20"
        artist = "Kimonas Theodossiou"
        imageUri = "https://cards.scryfall.io/normal/front/b/c/bcb00599-e082-49b0-88f3-ef91b75595e4.jpg?1783931088"
    }
}
