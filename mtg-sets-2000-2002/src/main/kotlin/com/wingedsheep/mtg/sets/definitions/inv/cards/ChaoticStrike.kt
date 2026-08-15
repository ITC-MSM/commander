package com.wingedsheep.mtg.sets.definitions.inv.cards

import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.FlipCoinEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Chaotic Strike
 * {1}{R}
 * Instant
 * Cast this spell only during combat after blockers are declared.
 * Flip a coin. If you win the flip, target creature gets +1/+1 until end of turn.
 * Draw a card.
 *
 * Timing note: modeled as "cast only during the declare blockers step" — the canonical
 * "after blockers are declared" window. The engine has no general "blockers declared"
 * cast condition, so the rarely-used combat-damage-step window is not exposed.
 */
val ChaoticStrike = card("Chaotic Strike") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Instant"
    oracleText = "Cast this spell only during combat after blockers are declared.\n" +
        "Flip a coin. If you win the flip, target creature gets +1/+1 until end of turn.\n" +
        "Draw a card."

    spell {
        castOnlyDuring(Step.DECLARE_BLOCKERS)
        target = Targets.Creature
        effect = FlipCoinEffect(
            wonEffect = Effects.ModifyStats(1, 1, EffectTarget.ContextTarget(0))
        ) then Effects.DrawCards(1)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "140"
        artist = "Massimiliano Frezzato"
        imageUri = "https://cards.scryfall.io/normal/front/0/6/061df8e4-6947-4bbb-9fe7-52ca4fd95d65.jpg?1762258062"
    }
}
