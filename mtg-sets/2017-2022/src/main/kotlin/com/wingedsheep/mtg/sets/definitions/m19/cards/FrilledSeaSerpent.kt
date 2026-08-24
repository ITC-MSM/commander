package com.wingedsheep.mtg.sets.definitions.m19.cards

import com.wingedsheep.sdk.core.AbilityFlag
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Frilled Sea Serpent
 * {4}{U}{U}
 * Creature — Serpent
 * 4/6
 * {5}{U}{U}: This creature can't be blocked this turn.
 */
val FrilledSeaSerpent = card("Frilled Sea Serpent") {
    manaCost = "{4}{U}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Serpent"
    power = 4
    toughness = 6
    oracleText = "{5}{U}{U}: This creature can't be blocked this turn."

    activatedAbility {
        cost = Costs.Mana("{5}{U}{U}")
        effect = Effects.GrantKeyword(AbilityFlag.CANT_BE_BLOCKED, EffectTarget.Self)
        description = "{5}{U}{U}: This creature can't be blocked this turn."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "56"
        artist = "Steven Belledin"
        flavorText = "\"Reel it in. No, wait! Throw it back!\"\n" +
            "—Gertrude, deep-sea angler"
        imageUri = "https://cards.scryfall.io/normal/front/e/0/e0aa62c0-d24b-4bce-9f2b-d42402b0830c.jpg"
    }
}
