package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

val RampartHunter = card("Rampart Hunter") {
    manaCost = "{3}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Horror"
    oracleText = "Deathtouch\nWhen this creature enters, target creature gets +2/+2 and gains deathtouch until end of turn."
    power = 3
    toughness = 3

    keywords(Keyword.DEATHTOUCH)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val creature = target("creature", Targets.Creature)
        effect = Effects.Composite(
            Effects.ModifyStats(2, 2, creature),
            Effects.GrantKeyword(Keyword.DEATHTOUCH, creature)
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "60"
        artist = "Néstor Ossandón Leal"
        flavorText = "The Eradia surrounding Hexhaven is rife with mage hunters drawn to the powerful magic emanating from the school's tower."
        imageUri = "https://cards.scryfall.io/normal/front/f/4/f4a80225-7459-4151-86bb-8fdea31c39a6.jpg?1789127211"
        inBooster = false
    }
}
