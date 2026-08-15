package com.wingedsheep.mtg.sets.definitions.ktk.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.MarkExileControllerGraveyardOnDeathEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Burn Away
 * {4}{R}
 * Instant
 * Burn Away deals 6 damage to target creature. When that creature dies this turn,
 * exile its controller's graveyard.
 */
val BurnAway = card("Burn Away") {
    manaCost = "{4}{R}"
    colorIdentity = "R"
    typeLine = "Instant"
    oracleText = "Burn Away deals 6 damage to target creature. When that creature dies this turn, exile its controller's graveyard."

    spell {
        val t = target("creature", Targets.Creature)
        effect = Effects.DealDamage(6, t) then
                MarkExileControllerGraveyardOnDeathEffect(t)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "104"
        artist = "Vincent Proce"
        flavorText = "\"Your corruption will burn, serpent, until there is nothing left to defile.\"\n—Asmala, bloodfire mystic"
        imageUri = "https://cards.scryfall.io/normal/front/f/4/f4b24e40-c0f0-4472-973e-d3b0e88fc93e.jpg?1562795996"
    }
}
