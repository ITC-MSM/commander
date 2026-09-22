package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

val GreenhousePropagator = card("Greenhouse Propagator") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Cat Druid"
    oracleText = "Whenever another creature you control enters, you gain 1 life.\n{T}: Add {G}."
    power = 2
    toughness = 3

    triggeredAbility {
        trigger = Triggers.OtherCreatureEnters
        effect = Effects.GainLife(1)
    }

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddMana(Color.GREEN)
        manaAbility = true
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "104"
        artist = "Ekaterina Burmak"
        flavorText = "Vigorbloom's unique graft technique requires the tender nurturing of seedlings to root seamlessly into the body and facilitate healing."
        imageUri = "https://cards.scryfall.io/normal/front/a/5/a56e0f91-b128-4693-a949-53cb403f4fbf.jpg?1789127136"
        inBooster = false
    }
}
