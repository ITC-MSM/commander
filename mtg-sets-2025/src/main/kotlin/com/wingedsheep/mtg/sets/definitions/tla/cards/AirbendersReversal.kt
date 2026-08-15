package com.wingedsheep.mtg.sets.definitions.tla.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.Mode
import com.wingedsheep.sdk.scripting.effects.ModalEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Airbender's Reversal — {1}{W} Instant — Lesson
 *
 * Choose one —
 * • Destroy target attacking creature.
 * • Airbend target creature you control. (Exile it. While it's exiled, its owner may cast it for
 *   {2} rather than its mana cost.)
 *
 * A `ModalEffect.chooseOne` with one target per mode. The airbend mode uses the target-agnostic
 * [Effects.Airbend]: the mode's `targetRequirements` populate the resolution context's targets, and
 * Airbend airbends them via `CardSource.ChosenTargets` (the same context the destroy mode reads via
 * `ContextTarget(0)`).
 */
val AirbendersReversal = card("Airbender's Reversal") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Instant — Lesson"
    oracleText = "Choose one —\n" +
        "• Destroy target attacking creature.\n" +
        "• Airbend target creature you control. (Exile it. While it's exiled, its owner may cast it for {2} rather than its mana cost.)"

    spell {
        effect = ModalEffect.chooseOne(
            Mode(
                effect = Effects.Destroy(EffectTarget.ContextTarget(0)),
                targetRequirements = listOf(Targets.AttackingCreature),
                description = "Destroy target attacking creature"
            ),
            Mode(
                effect = Effects.Airbend(),
                targetRequirements = listOf(Targets.CreatureYouControl),
                description = "Airbend target creature you control"
            )
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "7"
        artist = "Kotakan"
        flavorText = "Aang employed the typical Airbender tactics against Zuko: avoid and evade."
        imageUri = "https://cards.scryfall.io/normal/front/6/b/6b7078e4-2892-4b5f-83ab-90369e0d6dba.jpg?1764119914"
    }
}
