package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

val TheoristsProxy = card("Theorist's Proxy") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Illusion"
    oracleText = "Flash\n" +
        "When this creature enters, empower Jace 3. (Put three loyalty counters on a Jace token you control. If you don't control one, first create a blue Jace planeswalker token with \"[−1]: Surveil 1\" and \"[−3]: Draw a card.\")\n" +
        "{U}, Sacrifice this creature: The next spell you cast this turn can't be countered."
    power = 0
    toughness = 3

    keywords(Keyword.FLASH)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Patterns.Mechanic.empowerJace(3)
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{U}"), Costs.SacrificeSelf)
        effect = Effects.MakeNextSpellUncounterable()
        description = "The next spell you cast this turn can't be countered."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "44"
        artist = "Matt Stewart"
        imageUri = "https://cards.scryfall.io/normal/front/7/1/710302ca-c4be-4069-8ce1-f531414c74e9.jpg?1788878151"
        inBooster = false
    }
}
