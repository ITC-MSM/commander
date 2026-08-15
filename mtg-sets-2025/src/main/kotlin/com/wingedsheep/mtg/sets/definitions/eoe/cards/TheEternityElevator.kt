package com.wingedsheep.mtg.sets.definitions.eoe.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ActivationRestriction
import com.wingedsheep.sdk.scripting.events.CounterTypeFilter
import com.wingedsheep.sdk.scripting.values.DynamicAmount
import com.wingedsheep.sdk.scripting.values.EntityNumericProperty
import com.wingedsheep.sdk.scripting.values.EntityReference
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.station

/**
 * The Eternity Elevator
 * {5}
 * Legendary Artifact — Spacecraft
 * {T}: Add {C}{C}{C}.
 * Station (Tap another creature you control: Put charge counters equal to its power on this Spacecraft. Station only as a sorcery.)
 * 20+ | {T}: Add X mana of any one color, where X is the number of charge counters on The Eternity Elevator.
 */
val TheEternityElevator = card("The Eternity Elevator") {
    manaCost = "{5}"
    colorIdentity = ""
    typeLine = "Legendary Artifact — Spacecraft"
    oracleText = "{T}: Add {C}{C}{C}.\n" +
        "Station (Tap another creature you control: Put charge counters equal to its power on this Spacecraft. Station only as a sorcery.)\n" +
        "20+ | {T}: Add X mana of any one color, where X is the number of charge counters on The Eternity Elevator."

    // {T}: Add {C}{C}{C}.
    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddColorlessMana(3)
        manaAbility = true
    }

    // Station: tap another creature → add charge counters equal to its power
    station()

    // 20+ charge counters: {T}: Add X mana of any one color, X = charge counters on this
    val charge20 = Conditions.SourceCounterCountAtLeast(Counters.CHARGE, 20)

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddAnyColorMana(
            amount = DynamicAmount.EntityProperty(
                entity = EntityReference.Source,
                numericProperty = EntityNumericProperty.CounterCount(CounterTypeFilter.Named(Counters.CHARGE))
            )
        )
        manaAbility = true
        restrictions = listOf(ActivationRestriction.OnlyIfCondition(charge20))
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "241"
        artist = "Josu Solano"
        imageUri = "https://cards.scryfall.io/normal/front/1/b/1bb90ab9-43b9-4991-806a-0afc4d8caf5f.jpg?1755341317"
    }
}
