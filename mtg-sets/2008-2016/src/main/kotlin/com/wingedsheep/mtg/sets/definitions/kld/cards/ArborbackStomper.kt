package com.wingedsheep.mtg.sets.definitions.kld.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Arborback Stomper
 * {3}{G}{G}
 * Creature — Beast
 * 5/4
 * Trample
 * When this creature enters, you gain 5 life.
 *
 * Trample is a plain keyword; the life gain is an untargeted [Triggers.EntersBattlefield] trigger,
 * so it resolves for the controller with no target slot.
 */
val ArborbackStomper = card("Arborback Stomper") {
    manaCost = "{3}{G}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Beast"
    oracleText = "Trample\n" +
        "When this creature enters, you gain 5 life."
    power = 5
    toughness = 4

    keywords(Keyword.TRAMPLE)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.GainLife(5)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "142"
        artist = "Dan Murayama Scott"
        flavorText = "The power of aether draws creatures to it like a magnetic force, and those that are touched by it are forever altered."
        imageUri = "https://cards.scryfall.io/normal/front/7/8/788b9d55-6679-4fcc-a3af-11d31e477421.jpg?1783937184"
    }
}
