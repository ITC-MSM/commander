package com.wingedsheep.mtg.sets.definitions.rna.cards

import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Sage's Row Savant
 * {1}{U}
 * Creature — Vedalken Wizard
 * 2/1
 * When this creature enters, scry 2.
 */
val SagesRowSavant = card("Sage's Row Savant") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Vedalken Wizard"
    power = 2
    toughness = 1
    oracleText = "When this creature enters, scry 2."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Patterns.Library.scry(2)
        description = "When this creature enters, scry 2."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "49"
        artist = "Bastien L. Deharme"
        flavorText = "The streets of Ravnica are full of former guild members now using their institutional skills for personal gain."
        imageUri = "https://cards.scryfall.io/normal/front/d/5/d573626b-e7fa-4c31-a3d4-b853adfe787e.jpg?1783933705"
    }
}
