package com.wingedsheep.mtg.sets.definitions.xln.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Deeproot Champion
 * {1}{G}
 * Creature — Merfolk Shaman
 * 1/1
 *
 * Whenever you cast a noncreature spell, put a +1/+1 counter on this creature.
 */
val DeeprootChampion = card("Deeproot Champion") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Merfolk Shaman"
    oracleText = "Whenever you cast a noncreature spell, put a +1/+1 counter on this creature."
    power = 1
    toughness = 1

    triggeredAbility {
        trigger = Triggers.YouCastNoncreature
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "185"
        artist = "Raymond Swanland"
        flavorText = "\"No good will come from what you seek. Turn back now or suffer an ignoble death far from your home.\""
        imageUri = "https://cards.scryfall.io/normal/front/e/a/ea1e7fed-e6f3-4445-8d01-dca3971f726f.jpg"
    }
}
