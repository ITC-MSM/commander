package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

val KeeperOfTheQuietHour = card("Keeper of the Quiet Hour") {
    manaCost = "{3}"
    typeLine = "Artifact Creature — Chimera"
    oracleText = "When this creature enters, empower Jace 2. (Put two loyalty counters on a Jace token you control. If you don't control one, first create a blue Jace planeswalker token with \"[−1]: Surveil 1\" and \"[−3]: Draw a card.\")"
    power = 3
    toughness = 2

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Patterns.Mechanic.empowerJace(2)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "171"
        artist = "John Tedrick"
        flavorText = "Late at night it searches for students congregating where they shouldn't be."
        imageUri = "https://cards.scryfall.io/normal/front/b/6/b6331218-dbb8-44a9-8ba5-5fb1ab41c5c0.jpg?1788878239"
        inBooster = false
    }
}
