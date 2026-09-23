package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.TargetCreature

val AcademicAscent = card("Academic Ascent") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Instant"
    oracleText = "Target creature gets +2/+2 and gains flying until end of turn.\n" +
        "Empower Jace 2. (Put two loyalty counters on a Jace token you control. If you don't control one, first create a blue Jace planeswalker token with \"[−1]: Surveil 1\" and \"[−3]: Draw a card.\")"

    spell {
        val creature = target("target creature", TargetCreature())
        effect = Effects.Composite(
            Effects.ModifyStats(2, 2, creature),
            Effects.GrantKeyword(Keyword.FLYING, creature),
            Patterns.Mechanic.empowerJace(2)
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "2"
        artist = "Randy Gallegos"
        flavorText = "The Theorist takes notice of innovative battlemages."
        imageUri = "https://cards.scryfall.io/normal/front/7/3/730d8c28-1e58-4b8e-89e9-445d154d2e83.jpg?1788878081"
        inBooster = false
    }
}
