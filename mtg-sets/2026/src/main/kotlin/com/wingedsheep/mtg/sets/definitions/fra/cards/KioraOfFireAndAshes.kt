package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

val KioraOfFireAndAshes = card("Kiora of Fire and Ashes") {
    manaCost = "{4}{R}{R}"
    colorIdentity = "R"
    typeLine = "Legendary Creature — Merfolk Noble"
    power = 2
    toughness = 2
    oracleText = "When Kiora enters, create a 5/5 red Dragon creature token with flying.\n{8}: Create a 5/5 red Dragon creature token with flying."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.CreateToken(power = 5, toughness = 5, colors = setOf(Color.RED), creatureTypes = setOf("Dragon"), keywords = setOf(Keyword.FLYING), imageUri = "https://cards.scryfall.io/normal/front/9/9/991a5840-adc7-45b5-8d5f-b24dae384bab.jpg?1789734473")
    }
    activatedAbility {
        cost = Costs.Mana("{8}")
        effect = Effects.CreateToken(power = 5, toughness = 5, colors = setOf(Color.RED), creatureTypes = setOf("Dragon"), keywords = setOf(Keyword.FLYING), imageUri = "https://cards.scryfall.io/normal/front/9/9/991a5840-adc7-45b5-8d5f-b24dae384bab.jpg?1789734473")
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "247"
        artist = "Chris Rahn"
        flavorText = "\"If you can't stand the heat, get out of my way.\""
        imageUri = "https://cards.scryfall.io/normal/front/0/8/08657053-86f9-4c52-abf0-d9cdd443ae3b.jpg?1789127995"
        inBooster = false
    }
}
