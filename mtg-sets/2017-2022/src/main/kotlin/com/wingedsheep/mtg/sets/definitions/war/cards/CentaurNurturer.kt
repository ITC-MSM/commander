package com.wingedsheep.mtg.sets.definitions.war.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Centaur Nurturer
 * {3}{G}
 * Creature — Centaur Druid
 * 2/4
 * When this creature enters, you gain 3 life.
 * {T}: Add one mana of any color.
 */
val CentaurNurturer = card("Centaur Nurturer") {
    manaCost = "{3}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Centaur Druid"
    oracleText = "When this creature enters, you gain 3 life.\n" +
        "{T}: Add one mana of any color."
    power = 2
    toughness = 4

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.GainLife(3)
    }

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddAnyColorMana()
        manaAbility = true
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "156"
        artist = "Even Amundsen"
        flavorText = "\"I call upon the vigor of the grasses, the hopes of the flowers, and the dreams of the trees.\""
        imageUri = "https://cards.scryfall.io/normal/front/b/f/bf020acb-e0c6-43b4-8324-0f2ec68b73d6.jpg"
    }
}
