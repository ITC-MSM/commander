package com.wingedsheep.mtg.sets.definitions.leg.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Emerald Dragonfly
 * {1}{G}
 * Creature — Insect
 * 1/1
 *
 * Flying
 * {G}{G}: This creature gains first strike until end of turn.
 */
val EmeraldDragonfly = card("Emerald Dragonfly") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Insect"
    power = 1
    toughness = 1
    oracleText = "Flying\n{G}{G}: This creature gains first strike until end of turn."

    keywords(Keyword.FLYING)
    activatedAbility {
        cost = Costs.Mana("{G}{G}")
        effect = Effects.GrantKeyword(Keyword.FIRST_STRIKE, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "184"
        artist = "Quinton Hoover"
        flavorText = "\"Flittering, wheeling, / darting in to strike, and then / gone just as you blink.\" " +
            "—\"Dragonfly Haiku,\" poet unknown"
        imageUri = "https://cards.scryfall.io/normal/front/a/3/a3e81250-52c3-49f6-be43-17c34339e177.jpg?1783948048"
    }
}
