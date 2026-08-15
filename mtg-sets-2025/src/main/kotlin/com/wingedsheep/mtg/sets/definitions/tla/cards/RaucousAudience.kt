package com.wingedsheep.mtg.sets.definitions.tla.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect

/**
 * Raucous Audience
 * Creature — Human Citizen
 *
 * {T}: Add {G}. If you control a creature with power 4 or greater, add {G}{G} instead.
 *
 * A mana ability whose output is gated on a state test evaluated at resolution: if you control a
 * creature with power 4 or greater it produces {G}{G}, otherwise {G}. Modeled as a [ConditionalEffect]
 * over the projected battlefield ([GameObjectFilter.Creature.powerAtLeast]) so power-modifying effects
 * are honored.
 */
val RaucousAudience = card("Raucous Audience") {
    typeLine = "Creature — Human Citizen"
    manaCost = "{1}{G}"
    colorIdentity = "G"
    power = 2
    toughness = 1
    oracleText = "{T}: Add {G}. If you control a creature with power 4 or greater, add {G}{G} instead."

    activatedAbility {
        cost = Costs.Tap
        effect = ConditionalEffect(
            condition = Conditions.YouControl(GameObjectFilter.Creature.powerAtLeast(4)),
            effect = Effects.AddMana(Color.GREEN, 2),
            elseEffect = Effects.AddMana(Color.GREEN),
        )
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "190"
        artist = "ikeda_cpt"
        flavorText = "\"I think The Boulder is going to win back the belt at Earth Rumble Six!\""
        imageUri = "https://cards.scryfall.io/normal/front/c/c/ccd03ec3-0fae-48b2-9f04-9c57d2f94267.jpg?1764121294"
    }
}
