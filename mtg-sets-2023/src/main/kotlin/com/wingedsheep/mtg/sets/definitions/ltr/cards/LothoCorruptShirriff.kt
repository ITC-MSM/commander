package com.wingedsheep.mtg.sets.definitions.ltr.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Lotho, Corrupt Shirriff
 * {W}{B}
 * Legendary Creature — Halfling Rogue
 * 2/1
 *
 * Whenever a player casts their second spell each turn, you lose 1 life and create a Treasure token.
 */
val LothoCorruptShirriff = card("Lotho, Corrupt Shirriff") {
    manaCost = "{W}{B}"
    colorIdentity = "BW"
    typeLine = "Legendary Creature — Halfling Rogue"
    power = 2
    toughness = 1
    oracleText = "Whenever a player casts their second spell each turn, you lose 1 life and create a Treasure token. " +
        "(It's an artifact with \"{T}, Sacrifice this token: Add one mana of any color.\")"

    triggeredAbility {
        trigger = Triggers.NthSpellCast(2)
        effect = Effects.LoseLife(1, EffectTarget.Controller) then Effects.CreateTreasure(1)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "213"
        artist = "Ilker Yildiz"
        flavorText = "\"Whatever is wrong in the Shire, Lotho will be at the bottom of it: you can be sure of that.\"\n—Pippin"
        imageUri = "https://cards.scryfall.io/normal/front/c/e/ce01ff8f-a037-484f-9148-c847ffaabc5a.jpg?1686969873"
    }
}
