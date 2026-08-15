package com.wingedsheep.mtg.sets.definitions.ltr.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.Duration
import com.wingedsheep.sdk.scripting.effects.MarkExileOnDeathEffect

/**
 * Smite the Deathless
 * {1}{R}
 * Instant
 *
 * Smite the Deathless deals 3 damage to target creature. That creature loses indestructible
 * until end of turn. If that creature would die this turn, exile it instead.
 */
val SmiteTheDeathless = card("Smite the Deathless") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Instant"
    oracleText = "Smite the Deathless deals 3 damage to target creature. That creature loses indestructible until end of turn. If that creature would die this turn, exile it instead."

    spell {
        val creature = target("target creature", Targets.Creature)
        // Remove indestructible and set up the "exile if it would die" replacement before
        // dealing damage so the damage can actually destroy/exile the creature.
        effect = Effects.RemoveKeyword(Keyword.INDESTRUCTIBLE, creature, Duration.EndOfTurn)
            .then(MarkExileOnDeathEffect(creature))
            .then(Effects.DealDamage(3, creature))
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "148"
        artist = "Alexander Mokhov"
        flavorText = "\"No living man am I!\""
        imageUri = "https://cards.scryfall.io/normal/front/8/b/8b716fcc-c4cc-4987-be82-2897a9888d23.jpg?1686969173"
    }
}
