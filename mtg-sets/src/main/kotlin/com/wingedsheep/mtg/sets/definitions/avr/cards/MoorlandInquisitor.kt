package com.wingedsheep.mtg.sets.definitions.avr.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.Duration
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Moorland Inquisitor
 * {1}{W}
 * Creature — Human Soldier
 * 2/2
 * {2}{W}: This creature gains first strike until end of turn.
 */
val MoorlandInquisitor = card("Moorland Inquisitor") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Soldier"
    oracleText =
        "{2}{W}: This creature gains first strike until end of turn. " +
            "(It deals combat damage before creatures without first strike.)"
    power = 2
    toughness = 2

    activatedAbility {
        cost = Costs.Mana("{2}{W}")
        effect = Effects.GrantKeyword(Keyword.FIRST_STRIKE, EffectTarget.Self, Duration.EndOfTurn)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "30"
        artist = "David Palumbo"
        flavorText =
            "Inquisitors are taught scripture, philosophy, and the fine art of sharpening an axe."
        imageUri =
            "https://cards.scryfall.io/normal/front/5/8/581dbbea-9995-4e4b-ba5c-d6d5597e4ace.jpg?1783940731"
    }
}
