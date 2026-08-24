package com.wingedsheep.mtg.sets.definitions.tmp.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Energizer
 * {4}
 * Artifact Creature — Juggernaut
 * 2/2
 * {2}, {T}: Put a +1/+1 counter on this creature.
 */
val Energizer = card("Energizer") {
    manaCost = "{4}"
    colorIdentity = ""
    typeLine = "Artifact Creature — Juggernaut"
    power = 2
    toughness = 2
    oracleText = "{2}, {T}: Put a +1/+1 counter on this creature."

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{2}"), Costs.Tap)
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "285"
        artist = "Val Mayerik"
        flavorText = "Like a kettle set to boil, it must eventually go off—on someone."
        imageUri = "https://cards.scryfall.io/normal/front/9/1/914f204c-7f3d-41f2-a771-0b6227d539eb.jpg"
    }
}
