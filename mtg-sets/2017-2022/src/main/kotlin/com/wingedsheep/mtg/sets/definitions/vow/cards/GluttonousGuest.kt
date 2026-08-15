package com.wingedsheep.mtg.sets.definitions.vow.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Gluttonous Guest
 * {2}{B}
 * Creature — Vampire
 * 1/4
 * When this creature enters, create a Blood token.
 * Whenever you sacrifice a Blood token, you gain 1 life.
 */
val GluttonousGuest = card("Gluttonous Guest") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Vampire"
    oracleText = "When this creature enters, create a Blood token. (It's an artifact with \"{1}, {T}, Discard a card, Sacrifice this token: Draw a card.\")\n" +
        "Whenever you sacrifice a Blood token, you gain 1 life."
    power = 1
    toughness = 4

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.CreateBlood()
    }

    triggeredAbility {
        trigger = Triggers.YouSacrificeOneOrMore(GameObjectFilter.Artifact.withSubtype("Blood"))
        effect = Effects.GainLife(1)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "114"
        artist = "Jesper Ejsing"
        imageUri = "https://cards.scryfall.io/normal/front/1/8/18c07288-1c71-4e71-bdf5-910eb583a1d8.jpg?1782703110"
    }
}
