package com.wingedsheep.mtg.sets.definitions.som.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Saberclaw Golem — Scars of Mirrodin #200
 * {5} · Artifact Creature — Golem · 4 / 2
 *
 * {R}: This creature gains first strike until end of turn.
 *
 * A colourless artifact creature with a red activation cost, so its colour identity is red while
 * the card itself stays colourless.
 */
val SaberclawGolem = card("Saberclaw Golem") {
    manaCost = "{5}"
    colorIdentity = "R"
    typeLine = "Artifact Creature — Golem"
    power = 4
    toughness = 2
    oracleText = "{R}: This creature gains first strike until end of turn."

    activatedAbility {
        cost = Costs.Mana("{R}")
        effect = Effects.GrantKeyword(Keyword.FIRST_STRIKE, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "200"
        artist = "Mike Bierek"
        flavorText = "The warriors of the Blade Tribe charged the golem, twenty strong. They returned numbering ten . . . and a half."
        imageUri = "https://cards.scryfall.io/normal/front/6/6/6656b6d1-1c92-4da4-8afb-36f11610b0b4.jpg?1783941697"
    }
}
