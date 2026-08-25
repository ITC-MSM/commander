package com.wingedsheep.mtg.sets.definitions.leg.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Cyclopean Mummy
 * {1}{B}
 * Creature — Zombie
 * 2/1
 *
 * When this creature dies, exile it.
 *
 * A dies trigger keeps the default `activeZones = {BATTLEFIELD}`: it looks back in time
 * (CR 603.10a) and is indexed off the battlefield, so narrowing it to the graveyard with
 * `triggerZone` would stop `TriggerDetector` from ever indexing it.
 */
val CyclopeanMummy = card("Cyclopean Mummy") {
    manaCost = "{1}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Zombie"
    power = 2
    toughness = 1
    oracleText = "When this creature dies, exile it."

    triggeredAbility {
        trigger = Triggers.Dies
        effect = Effects.Exile(EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "93"
        artist = "Edward P. Beard, Jr."
        flavorText = "The ritual of plucking out an eye to gain future sight is but a curse that enables the " +
            "living to see their own deaths."
        imageUri = "https://cards.scryfall.io/normal/front/4/7/479ccc50-2d72-4adc-901e-fbd4eef2cf92.jpg?1783948068"
    }
}
