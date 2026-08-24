package com.wingedsheep.mtg.sets.definitions.ice.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TimingRule

/**
 * Orcish Lumberjack
 * {R}
 * Creature — Orc
 * 1/1
 *
 * {T}, Sacrifice a Forest: Add three mana in any combination of {R} and/or {G}.
 *
 * The same shape as Goblin Clearcutter: a two-atom activation cost ({T} plus a sacrifice whose
 * filter is a Forest *land*, not merely something named Forest) over
 * [Effects.AddManaInAnyCombination], which lets the player pick each of the three pips' colour at
 * resolution. `manaAbility = true` with [TimingRule.ManaAbility] keeps it usable mid-payment, which
 * is the whole point of the card.
 */
val OrcishLumberjack = card("Orcish Lumberjack") {
    manaCost = "{R}"
    colorIdentity = "GR"
    typeLine = "Creature — Orc"
    power = 1
    toughness = 1
    oracleText = "{T}, Sacrifice a Forest: Add three mana in any combination of {R} and/or {G}."

    activatedAbility {
        cost = Costs.Composite(
            Costs.Tap,
            Costs.Sacrifice(GameObjectFilter.Land.withSubtype("Forest"))
        )
        effect = Effects.AddManaInAnyCombination(3, setOf(Color.RED, Color.GREEN))
        manaAbility = true
        timing = TimingRule.ManaAbility
        description = "{T}, Sacrifice a Forest: Add three mana in any combination of {R} and/or {G}."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "210"
        artist = "Dan Frazier"
        flavorText = "\"How did I ever let myself get talked into this project?\"\n—Toothlicker Harj, Orcish Captain"
        imageUri = "https://cards.scryfall.io/normal/front/2/1/21ef13e3-658c-43a3-a290-4c5dde8e8b55.jpg"
    }
}
