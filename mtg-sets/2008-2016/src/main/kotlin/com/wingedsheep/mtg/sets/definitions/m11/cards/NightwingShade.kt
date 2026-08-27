package com.wingedsheep.mtg.sets.definitions.m11.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Nightwing Shade
 * {4}{B}
 * Creature — Shade
 * 2/2
 *
 * Flying
 * {1}{B}: This creature gets +1/+1 until end of turn.
 *
 * The standard Shade pump — [Effects.ModifyStats] on [EffectTarget.Self] behind a
 * [Costs.Mana], the same shape as Rise of the Eldrazi's Zof Shade — plus flying.
 */
val NightwingShade = card("Nightwing Shade") {
    manaCost = "{4}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Shade"
    power = 2
    toughness = 2
    oracleText = "Flying\n" +
        "{1}{B}: This creature gets +1/+1 until end of turn."

    keywords(Keyword.FLYING)

    activatedAbility {
        cost = Costs.Mana("{1}{B}")
        effect = Effects.ModifyStats(1, 1, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "109"
        artist = "Lucas Graciano"
        flavorText = "\"There is one hour of the night even we do not watch.\"\n" +
            "—Sedva, captain of the watch"
        imageUri = "https://cards.scryfall.io/normal/front/b/a/ba6232c3-f840-450a-8583-540aec0f17ed.jpg?1783941813"
    }
}
