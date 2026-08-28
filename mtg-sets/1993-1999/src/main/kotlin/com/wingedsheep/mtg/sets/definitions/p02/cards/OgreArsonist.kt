package com.wingedsheep.mtg.sets.definitions.p02.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Ogre Arsonist
 * {4}{R}
 * Creature — Ogre
 * 3 / 3
 *
 * When this creature enters, destroy target land.
 */
val OgreArsonist = card("Ogre Arsonist") {
    manaCost = "{4}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Ogre"
    oracleText = "When this creature enters, destroy target land."
    power = 3
    toughness = 3

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val land = target("target", Targets.Land)
        effect = Effects.Destroy(land)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "110"
        artist = "Jeffrey R. Busch"
        imageUri = "https://cards.scryfall.io/normal/front/b/6/b67e28b2-9d25-4873-8db2-1f0853ab0c47.jpg"
    }
}
