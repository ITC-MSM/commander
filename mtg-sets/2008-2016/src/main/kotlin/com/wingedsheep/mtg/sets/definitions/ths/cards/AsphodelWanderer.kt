package com.wingedsheep.mtg.sets.definitions.ths.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.RegenerateEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Asphodel Wanderer
 * {B}
 * Creature — Skeleton Soldier
 * 1 / 1
 *
 * {2}{B}: Regenerate this creature.
 *
 * There is no `Effects.Regenerate` facade — [RegenerateEffect] on [EffectTarget.Self] is the shipped
 * spelling (Cudgel Troll, Cinderbones, Kin-Tree Warden).
 */
val AsphodelWanderer = card("Asphodel Wanderer") {
    manaCost = "{B}"
    colorIdentity = "B"
    typeLine = "Creature — Skeleton Soldier"
    power = 1
    toughness = 1
    oracleText = "{2}{B}: Regenerate this creature."

    activatedAbility {
        cost = Costs.Mana("{2}{B}")
        effect = RegenerateEffect(EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "77"
        artist = "Scott Chou"
        flavorText = "He killed out of hate, so now only hate sustains him. He sought immortality, so the gods gave it to him."
        imageUri = "https://cards.scryfall.io/normal/front/6/7/675112db-c477-43f4-b755-54162445fb49.jpg"
    }
}
