package com.wingedsheep.mtg.sets.definitions.lci.cards

import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Brazen Blademaster
 * {2}{R}
 * Creature — Orc Pirate
 * 2/3
 * Whenever this creature attacks while you control two or more artifacts,
 * it gets +2/+1 until end of turn.
 *
 * Implementation notes:
 * - `Triggers.Attacks` fires per AttackEvent for the creature itself (SELF binding).
 * - "while you control two or more artifacts" is a trigger-time condition, NOT an intervening-if
 *   clause (CR 603.4 applies only to an "if" immediately after the trigger event). It is checked
 *   as `Conditions.YouControlAtLeast(2, GameObjectFilter.Artifact)` only when the creature attacks;
 *   the engine's `triggerCondition` is evaluated at trigger detection, not re-checked on resolution
 *   — which matches the "while" semantics.
 * - `Effects.ModifyStats(+2, +1, EffectTarget.Self)` applies the bonus; the default duration
 *   is EndOfTurn, so it expires in the cleanup step.
 */
val BrazenBlademaster = card("Brazen Blademaster") {
    manaCost = "{2}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Orc Pirate"
    power = 2
    toughness = 3
    oracleText = "Whenever this creature attacks while you control two or more artifacts, it gets +2/+1 until end of turn."

    triggeredAbility {
        trigger = Triggers.Attacks
        triggerCondition = Conditions.YouControlAtLeast(2, GameObjectFilter.Artifact)
        effect = Effects.ModifyStats(power = 2, toughness = 1, target = EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "136"
        artist = "Jarel Threat"
        imageUri = "https://cards.scryfall.io/normal/front/8/e/8e9e0c66-f64a-428c-be13-a52691833df1.jpg?1782694500"
    }
}
