package com.wingedsheep.mtg.sets.definitions.m12.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Sorin's Thirst — Magic 2012 #110 (canonical printing; reprinted in WAR and M20)
 * {B}{B}
 * Instant
 * Sorin's Thirst deals 2 damage to target creature and you gain 2 life.
 *
 * Damage and life gain are two effects sharing one sentence, not a drain: the 2 life is gained
 * whether or not the damage kills, and it is gained even if the creature has left the
 * battlefield by resolution — as long as the spell still has a legal target.
 */
val SorinsThirst = card("Sorin's Thirst") {
    manaCost = "{B}{B}"
    colorIdentity = "B"
    typeLine = "Instant"
    oracleText = "Sorin's Thirst deals 2 damage to target creature and you gain 2 life."

    spell {
        val creature = target("target", Targets.Creature)
        effect = Effects.Composite(
            Effects.DealDamage(2, creature),
            Effects.GainLife(2)
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "110"
        artist = "Karl Kopinski"
        flavorText = "\"All your steel won't protect you if your will is weak.\""
        imageUri = "https://cards.scryfall.io/normal/front/1/f/1f14a435-811d-4057-93a9-ce74aa852a09.jpg"
    }
}
