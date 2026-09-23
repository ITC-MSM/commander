package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

val MindseekerOculus = card("Mindseeker Oculus") {
    manaCost = "{2}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Homunculus"
    oracleText = "When this creature enters, empower Jace 4. (Put four loyalty counters on a Jace token you control. If you don't control one, first create a blue Jace planeswalker token with \"[−1]: Surveil 1\" and \"[−3]: Draw a card.\")"
    power = 2
    toughness = 1

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Patterns.Mechanic.empowerJace(4)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "33"
        artist = "Ioannis Fiore"
        flavorText = "\"No need to sneak around. Beleren knows we're here.\"\n—Garruk, to Vraska"
        imageUri = "https://cards.scryfall.io/normal/front/f/5/f5324741-353a-4a70-adb2-b631b00806dd.jpg?1789556707"
        inBooster = false
    }
}
