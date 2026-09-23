package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

val ProtegesAwakening = card("Protege's Awakening") {
    manaCost = "{3}{U}"
    colorIdentity = "U"
    typeLine = "Sorcery"
    oracleText = "Empower Jace 6. (Put six loyalty counters on a Jace token you control. If you don't control one, first create a blue Jace planeswalker token with \"[−1]: Surveil 1\" and \"[−3]: Draw a card.\")\n" +
        "Draw a card."

    spell {
        effect = Patterns.Mechanic.empowerJace(6).then(Effects.DrawCards(1))
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "37"
        artist = "Cynthia Sheppard"
        flavorText = "\"Am I a person?\" she asked.\nJace smiled. \"Yes. Your name is Tamira. But who you are is up to you.\""
        imageUri = "https://cards.scryfall.io/normal/front/a/0/a08c7ec2-4c6a-4db2-85a7-41afe8731523.jpg?1788878176"
        inBooster = false
    }
}
