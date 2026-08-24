package com.wingedsheep.mtg.sets.definitions.m15.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Shaman of Spring
 * {3}{G}
 * Creature — Elf Shaman
 * 2/2
 * When this creature enters, draw a card.
 */
val ShamanOfSpring = card("Shaman of Spring") {
    manaCost = "{3}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Elf Shaman"
    power = 2
    toughness = 2
    oracleText = "When this creature enters, draw a card."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.DrawCards(1)
        description = "When this creature enters, draw a card."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "199"
        artist = "Johannes Voss"
        flavorText = "Some shamanic sects advocate the different seasons, each working to preserve nature's cycles."
        imageUri = "https://cards.scryfall.io/normal/front/8/e/8e974df6-d78a-43ea-ada5-17c53fcca97b.jpg?1783939162"
    }
}
