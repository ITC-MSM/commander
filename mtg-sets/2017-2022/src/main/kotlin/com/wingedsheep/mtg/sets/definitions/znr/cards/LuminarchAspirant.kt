package com.wingedsheep.mtg.sets.definitions.znr.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Luminarch Aspirant
 * {1}{W}
 * Creature — Human Cleric
 * 1/1
 *
 * At the beginning of combat on your turn, put a +1/+1 counter on target creature you control.
 */
val LuminarchAspirant = card("Luminarch Aspirant") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Cleric"
    power = 1
    toughness = 1
    oracleText = "At the beginning of combat on your turn, put a +1/+1 counter on target " +
        "creature you control."

    triggeredAbility {
        trigger = Triggers.BeginCombat
        target = Targets.CreatureYouControl
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.ContextTarget(0))
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "24"
        artist = "Mads Ahm"
        flavorText = "\"Rally to my light, and together we will drive out this darkness!\""
        imageUri = "https://cards.scryfall.io/normal/front/f/e/fe964e7e-e2c5-4263-889d-0a531eb51442.jpg"
    }
}
