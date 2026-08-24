package com.wingedsheep.mtg.sets.definitions.tmp.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.RegenerateEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Darkling Stalker
 * {3}{B}
 * Creature — Shade Spirit
 * 1/1
 * {B}: Regenerate this creature.
 * {B}: This creature gets +1/+1 until end of turn.
 */
val DarklingStalker = card("Darkling Stalker") {
    manaCost = "{3}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Shade Spirit"
    power = 1
    toughness = 1
    oracleText = "{B}: Regenerate this creature.\n" +
        "{B}: This creature gets +1/+1 until end of turn."

    activatedAbility {
        cost = Costs.Mana("{B}")
        effect = RegenerateEffect(EffectTarget.Self)
    }

    activatedAbility {
        cost = Costs.Mana("{B}")
        effect = Effects.ModifyStats(1, 1, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "119"
        artist = "Susan Van Camp"
        flavorText = "\"In this dark place, yes, I *am* afraid of my own shadow.\"\n" +
            "—Mirri of the *Weatherlight*"
        imageUri = "https://cards.scryfall.io/normal/front/4/e/4eb883b7-da6a-45c3-9dde-61334a0ddcae.jpg"
    }
}
