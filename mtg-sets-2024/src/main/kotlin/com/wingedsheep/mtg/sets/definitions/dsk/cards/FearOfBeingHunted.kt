package com.wingedsheep.mtg.sets.definitions.dsk.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.MustBeBlocked

/**
 * Fear of Being Hunted
 * {1}{R}{R}
 * Enchantment Creature — Nightmare
 * 4/2
 * Haste
 * This creature must be blocked if able.
 *
 * "Must be blocked if able" is the `MustBeBlocked` static ability (allCreatures = false: at least
 * one creature able to block it must do so), honored by the block-phase validation.
 */
val FearOfBeingHunted = card("Fear of Being Hunted") {
    manaCost = "{1}{R}{R}"
    colorIdentity = "R"
    typeLine = "Enchantment Creature — Nightmare"
    oracleText = "Haste\nThis creature must be blocked if able."
    power = 4
    toughness = 2

    keywords(Keyword.HASTE)

    staticAbility {
        ability = MustBeBlocked(allCreatures = false)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "134"
        artist = "Maxime Minard"
        flavorText = "It makes no effort to hide itself, savoring its prey's rising panic as the " +
            "sound of its clacking skull grows closer and closer."
        imageUri = "https://cards.scryfall.io/normal/front/a/0/a0a5e716-68c1-4fa0-aa08-5b08114e08d8.jpg?1726286347"
    }
}
