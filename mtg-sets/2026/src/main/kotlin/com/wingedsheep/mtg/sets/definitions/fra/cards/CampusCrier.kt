package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

val CampusCrier = card("Campus Crier") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Advisor"
    power = 3
    toughness = 1
    oracleText = "{1}, Exile this card from your graveyard: Empower Jace 2. (Put two loyalty counters on a Jace token you control. If you don't control one, first create a blue Jace planeswalker token with \"[−1]: Surveil 1\" and \"[−3]: Draw a card.\")"

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{1}"), Costs.ExileSelf)
        activateFromZone = Zone.GRAVEYARD
        effect = Patterns.Mechanic.empowerJace(2)
        description = "Empower Jace 2."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "4"
        artist = "Johan Grenier"
        flavorText = "He speaks what the futurescribes have written."
        imageUri = "https://cards.scryfall.io/normal/front/6/0/6047b14c-91d5-4f8e-af3f-057a541e2546.jpg?1788878120"
        inBooster = false
    }
}
