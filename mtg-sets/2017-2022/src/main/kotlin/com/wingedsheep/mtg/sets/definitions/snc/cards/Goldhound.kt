package com.wingedsheep.mtg.sets.definitions.snc.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.TimingRule

/**
 * Goldhound
 * {R}
 * Artifact Creature — Treasure Dog
 * 1/1
 * First strike
 * Menace (This creature can't be blocked except by two or more creatures.)
 * {T}, Sacrifice this creature: Add one mana of any color.
 */
val Goldhound = card("Goldhound") {
    manaCost = "{R}"
    colorIdentity = "R"
    typeLine = "Artifact Creature — Treasure Dog"
    power = 1
    toughness = 1
    oracleText = "First strike\nMenace (This creature can't be blocked except by two or more creatures.)\n{T}, Sacrifice this creature: Add one mana of any color."

    keywords(Keyword.FIRST_STRIKE, Keyword.MENACE)

    activatedAbility {
        cost = Costs.Composite(Costs.Tap, Costs.SacrificeSelf)
        effect = Effects.AddAnyColorMana(1)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "108"
        artist = "Donato Giancola"
        flavorText = "The staccato clatter of its metal claws on pavement is unmistakable."
        imageUri = "https://cards.scryfall.io/normal/front/c/0/c059e4b4-1542-4b5c-810a-9f0abac5792b.jpg?1783923118"
        ruling("2022-04-29", "Even when it is on a creature, Treasure is an artifact type and not a creature type. Similarly, Dog is always a creature type and not an artifact type.")
        ruling("2022-04-29", "Since Goldhound is a creature, its {T} ability can't be activated the turn that it enters the battlefield or the turn a player gains control of it unless it has haste.")
    }
}
