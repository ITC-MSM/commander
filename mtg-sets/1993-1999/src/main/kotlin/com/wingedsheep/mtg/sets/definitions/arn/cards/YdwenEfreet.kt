package com.wingedsheep.mtg.sets.definitions.arn.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.FlipCoinEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Ydwen Efreet
 * {R}{R}{R}
 * Creature — Efreet
 * 3/6
 * Whenever this creature blocks, flip a coin. If you lose the flip, remove this creature
 * from combat and it can't block this turn. Creatures it was blocking that had become
 * blocked by only this creature this combat become unblocked.
 *
 * The "become unblocked" clause is the pre-modern remove-from-combat behavior preserved on
 * the card; modern CR 509.1h normally keeps a creature blocked after all its blockers leave
 * combat. Toggled via [com.wingedsheep.sdk.scripting.effects.RemoveFromCombatEffect.unblockSoleBlockedAttackers].
 */
val YdwenEfreet = card("Ydwen Efreet") {
    manaCost = "{R}{R}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Efreet"
    power = 3
    toughness = 6
    oracleText = "Whenever this creature blocks, flip a coin. If you lose the flip, remove this creature from combat and it can't block this turn. Creatures it was blocking that had become blocked by only this creature this combat become unblocked."

    triggeredAbility {
        trigger = Triggers.Blocks
        effect = FlipCoinEffect(
            lostEffect = Effects
                .RemoveFromCombat(EffectTarget.Self, unblockSoleBlockedAttackers = true)
                .then(Effects.CantBlock(EffectTarget.Self)),
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "44"
        artist = "Drew Tucker"
        imageUri = "https://cards.scryfall.io/normal/front/e/f/efdba2a9-d171-45ed-8dd4-9d0046128f68.jpg?1562940066"
        ruling("2009-10-01", "The ability triggers any time Ydwen Efreet blocks.")
    }
}
