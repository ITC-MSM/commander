package com.wingedsheep.mtg.sets.definitions.tmp.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Telethopter
 * {4}
 * Artifact Creature — Thopter
 * 3/1
 * Tap an untapped creature you control: This creature gains flying until end of turn.
 */
val Telethopter = card("Telethopter") {
    manaCost = "{4}"
    colorIdentity = ""
    typeLine = "Artifact Creature — Thopter"
    power = 3
    toughness = 1
    oracleText = "Tap an untapped creature you control: This creature gains flying until end of turn."

    activatedAbility {
        cost = Costs.TapPermanents(1, GameObjectFilter.Creature)
        effect = Effects.GrantKeyword(Keyword.FLYING, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "311"
        artist = "Thomas M. Baxa"
        flavorText = "After losing several of the devices to midair collisions, Greven forbade moggs from operating telethopters."
        imageUri = "https://cards.scryfall.io/normal/front/7/7/77d26c29-cd98-446b-b4e1-687561ed6d3f.jpg"
    }
}
