package com.wingedsheep.mtg.sets.definitions.m15.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Blood Host
 * {3}{B}{B}
 * Creature — Vampire
 * 3/3
 *
 * {1}{B}, Sacrifice another creature: Put a +1/+1 counter on this creature and you gain 2 life.
 */
val BloodHost = card("Blood Host") {
    manaCost = "{3}{B}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Vampire"
    oracleText = "{1}{B}, Sacrifice another creature: Put a +1/+1 counter on this creature and you gain 2 life."
    power = 3
    toughness = 3

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{1}{B}"), Costs.SacrificeAnother(GameObjectFilter.Creature))
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.Self)
            .then(Effects.GainLife(2))
        description = "{1}{B}, Sacrifice another creature: Put a +1/+1 counter on this creature and " +
            "you gain 2 life."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "87"
        artist = "Cynthia Sheppard"
        flavorText = "It would be ill-mannered to decline his invitation. It would be ill-advised to accept it."
        imageUri = "https://cards.scryfall.io/normal/front/1/4/1454a83d-f018-446c-89fb-21460924e589.jpg?1783939186"
    }
}
