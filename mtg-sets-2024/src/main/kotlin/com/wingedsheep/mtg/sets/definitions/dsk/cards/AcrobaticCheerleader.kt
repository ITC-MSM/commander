package com.wingedsheep.mtg.sets.definitions.dsk.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Acrobatic Cheerleader
 * {1}{W}
 * Creature — Human Survivor
 * 2/2
 * Survival — At the beginning of your second main phase, if this creature is tapped, put a
 * flying counter on it. This ability triggers only once.
 */
val AcrobaticCheerleader = card("Acrobatic Cheerleader") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Survivor"
    power = 2
    toughness = 2
    oracleText = "Survival — At the beginning of your second main phase, if this creature is " +
        "tapped, put a flying counter on it. This ability triggers only once."

    // Survival — At the beginning of your second main phase, if this creature is tapped, put a
    // flying counter on it. "This ability triggers only once" → triggersOnce caps it for the
    // permanent's lifetime (a flying counter grants flying via the keyword-counter projection).
    triggeredAbility {
        trigger = Triggers.YourPostcombatMain
        triggerCondition = Conditions.SourceIsTapped
        triggersOnce = true
        effect = Effects.AddCounters(Counters.FLYING, 1, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "1"
        artist = "Julia Metzger"
        flavorText = "\"I used to cheat death every day in practice. This is nothing.\""
        imageUri = "https://cards.scryfall.io/normal/front/6/f/6f1a7590-3eee-4803-b192-d4fb771e6a86.jpg?1726285858"
    }
}
