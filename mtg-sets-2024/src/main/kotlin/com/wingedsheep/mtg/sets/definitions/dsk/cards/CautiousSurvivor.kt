package com.wingedsheep.mtg.sets.definitions.dsk.cards

import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Cautious Survivor
 * {3}{G}
 * Creature — Elf Survivor
 * 4/4
 * Survival — At the beginning of your second main phase, if this creature is tapped, you gain 2 life.
 */
val CautiousSurvivor = card("Cautious Survivor") {
    manaCost = "{3}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Elf Survivor"
    power = 4
    toughness = 4
    oracleText = "Survival — At the beginning of your second main phase, if this creature is tapped, you gain 2 life."

    // Survival — At the beginning of your second main phase, if this creature is tapped, you gain 2 life.
    triggeredAbility {
        trigger = Triggers.YourPostcombatMain
        triggerCondition = Conditions.SourceIsTapped
        effect = Effects.GainLife(2)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "172"
        artist = "Jodie Muir"
        flavorText = "She came back alone, with her recorder broken and its footage scrambled. Whatever she had experienced, she never spoke of again."
        imageUri = "https://cards.scryfall.io/normal/front/e/e/ee2b4c1a-e058-4e06-bc46-e250fd9c9b54.jpg?1726286500"
    }
}
