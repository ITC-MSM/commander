package com.wingedsheep.mtg.sets.definitions.ths.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.TimingRule

/**
 * Opaline Unicorn
 * {3}
 * Artifact Creature — Unicorn
 * 1 / 2
 *
 * {T}: Add one mana of any color.
 */
val OpalineUnicorn = card("Opaline Unicorn") {
    manaCost = "{3}"
    colorIdentity = ""
    typeLine = "Artifact Creature — Unicorn"
    power = 1
    toughness = 2
    oracleText = "{T}: Add one mana of any color."

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddAnyColorMana()
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "218"
        artist = "Christine Choi"
        flavorText = "Purphoros once loved Nylea, the god of the hunt. His passion inspired his most astounding works of art."
        imageUri = "https://cards.scryfall.io/normal/front/c/f/cfba304c-9cb8-4d5c-b70d-b7f61a365977.jpg"
    }
}
