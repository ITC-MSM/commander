package com.wingedsheep.mtg.sets.definitions.ecl.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Wanderbrine Trapper
 * {W}
 * Creature — Merfolk Scout
 * 2/1
 * {1}, {T}, Tap another untapped creature you control: Tap target creature an opponent controls.
 */
val WanderbrineTrapper = card("Wanderbrine Trapper") {
    manaCost = "{W}"
    colorIdentity = "W"
    typeLine = "Creature — Merfolk Scout"
    power = 2
    toughness = 1
    oracleText = "{1}, {T}, Tap another untapped creature you control: Tap target creature an opponent controls."

    activatedAbility {
        cost = Costs.Composite(
            Costs.Mana("{1}"),
            Costs.Tap,
            Costs.TapAnotherPermanent(GameObjectFilter.Creature)
        )
        val t = target("creature an opponent controls", Targets.CreatureOpponentControls)
        effect = Effects.Tap(t)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "42"
        artist = "Iris Compiet"
        flavorText = "Merrow nets snare struggling prey and panicking foe alike."
        imageUri = "https://cards.scryfall.io/normal/front/f/4/f4c134ed-adbf-4e88-80e0-75c176ce94c3.jpg?1767956997"
    }
}
