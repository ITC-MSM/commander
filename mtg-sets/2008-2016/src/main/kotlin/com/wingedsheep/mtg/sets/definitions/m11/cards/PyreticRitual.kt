package com.wingedsheep.mtg.sets.definitions.m11.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Pyretic Ritual
 * {1}{R}
 * Instant
 *
 * Add {R}{R}{R}.
 *
 * The red twin of Dark Ritual — a spell whose whole effect is [Effects.AddMana]. No restriction
 * and no rider: the mana is plain red mana that empties at the end of the step like any other.
 */
val PyreticRitual = card("Pyretic Ritual") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Instant"
    oracleText = "Add {R}{R}{R}."

    spell {
        effect = Effects.AddMana(Color.RED, 3)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "153"
        artist = "James Paick"
        flavorText = "The Multiverse is filled with limitless power just waiting for someone to reach out and seize it."
        imageUri = "https://cards.scryfall.io/normal/front/1/e/1e577638-a7ed-4bcc-90fb-0cffe87d5a28.jpg?1783941802"
    }
}
