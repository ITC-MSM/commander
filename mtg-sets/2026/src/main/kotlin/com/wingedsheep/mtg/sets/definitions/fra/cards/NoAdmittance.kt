package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

val NoAdmittance = card("No Admittance") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Sorcery"
    oracleText = "No Admittance deals 3 damage to any target.\n" +
        "Empower Jace 1. (Put a loyalty counter on a Jace token you control. If you don't control one, first create a blue Jace planeswalker token with \"[−1]: Surveil 1\" and \"[−3]: Draw a card.\")"

    spell {
        val t = target("any target", Targets.Any)
        effect = Effects.DealDamage(3, t).then(Patterns.Mechanic.empowerJace(1))
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "89"
        artist = "Serena Malyon"
        imageUri = "https://cards.scryfall.io/normal/front/1/1/11ba4fdd-cc03-4bb6-a493-91a9785771d0.jpg?1788878170"
        inBooster = false
    }
}
