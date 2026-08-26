package com.wingedsheep.mtg.sets.definitions.akh.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Sparring Mummy
 * {3}{W}
 * Creature — Zombie
 * 3/3
 * When this creature enters, untap target creature.
 */
val SparringMummy = card("Sparring Mummy") {
    manaCost = "{3}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Zombie"
    oracleText = "When this creature enters, untap target creature."
    power = 3
    toughness = 3

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val creature = target("target creature", Targets.Creature)
        effect = Effects.Untap(creature)
        description = "When this creature enters, untap target creature."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "29"
        artist = "Ryan Pancoast"
        flavorText = "Aspiring to earn their place in the afterlife, acolytes train every day against those who fell short of that glory."
        imageUri = "https://cards.scryfall.io/normal/front/e/4/e4ec1587-0405-4b4d-96b5-1ac2c5a7c9dc.jpg?1783936532"
    }
}
