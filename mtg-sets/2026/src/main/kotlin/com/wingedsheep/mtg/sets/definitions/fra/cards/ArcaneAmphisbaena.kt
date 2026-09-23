package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

val ArcaneAmphisbaena = card("Arcane Amphisbaena") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Snake"
    oracleText = "Deathtouch\n" +
        "When this creature enters, empower Jace 2. (Put two loyalty counters on a Jace token you control. If you don't control one, first create a blue Jace planeswalker token with \"[−1]: Surveil 1\" and \"[−3]: Draw a card.\")"
    power = 1
    toughness = 1

    keywords(Keyword.DEATHTOUCH)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Patterns.Mechanic.empowerJace(2)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "97"
        artist = "Jason Mowry"
        flavorText = "Its tongues can taste loyalty—or deceit."
        imageUri = "https://cards.scryfall.io/normal/front/1/b/1bf923c4-f0b7-4271-978c-fd2e79fe1cc8.jpg?1788878190"
        inBooster = false
    }
}
