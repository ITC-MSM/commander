package com.wingedsheep.mtg.sets.definitions.rna.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.riot
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Frenzied Arynx — Ravnica Allegiance #173
 * {2}{R}{G} · Creature — Cat Beast · 3 / 3
 *
 * Riot, printed trample, and a repeatable firebreathing-style pump.
 */
val FrenziedArynx = card("Frenzied Arynx") {
    manaCost = "{2}{R}{G}"
    colorIdentity = "GR"
    typeLine = "Creature — Cat Beast"
    power = 3
    toughness = 3
    oracleText = "Riot (This creature enters with your choice of a +1/+1 counter or haste.)\n" +
        "Trample\n" +
        "{4}{R}{G}: This creature gets +3/+0 until end of turn."

    riot()
    keywords(Keyword.TRAMPLE)
    activatedAbility {
        cost = Costs.Mana("{4}{R}{G}")
        effect = Effects.ModifyStats(3, 0, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "173"
        artist = "Filip Burburan"
        imageUri = "https://cards.scryfall.io/normal/front/b/c/bce2eef7-03a4-415f-8bb7-a29d50ce1b0f.jpg"
    }
}
