package com.wingedsheep.mtg.sets.definitions.sos.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Goblin Glasswright // Craft with Pride — Secrets of Strixhaven #117
 * {1}{R} · Creature — Goblin Sorcerer · 2/2
 *
 * This creature enters prepared. (While it's prepared, you may cast a copy of its spell.
 * Doing so unprepares it.)
 * //
 * Craft with Pride — {R}, Sorcery: Create a Treasure token.
 *
 * Prepare (Secrets of Strixhaven): the creature enters with the PREPARED keyword. Becoming prepared
 * synthesizes a free-to-cast copy of its prepare spell ("Craft with Pride") in exile for {R};
 * casting that copy unprepares the creature. Modeled via [CardLayout.PREPARE] + the
 * `prepare(name) { }` DSL, same as Cheerful Osteomancer. The prepare spell side just makes a Treasure
 * via [Effects.CreateTreasure].
 */
val GoblinGlasswright = card("Goblin Glasswright") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Goblin Sorcerer"
    power = 2
    toughness = 2
    oracleText = "This creature enters prepared. (While it's prepared, you may cast a copy of its spell. Doing so unprepares it.)"

    keywords(Keyword.PREPARED)

    // Craft with Pride — the prepare spell. Create a Treasure token.
    prepare("Craft with Pride") {
        manaCost = "{R}"
        typeLine = "Sorcery"
        oracleText = "Create a Treasure token. (It's an artifact with \"{T}, Sacrifice this token: Add one mana of any color.\")"
        spell {
            effect = Effects.CreateTreasure()
        }
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "117"
        artist = "David Auden Nash"
        imageUri = "https://cards.scryfall.io/normal/front/c/8/c85c5f06-dd31-4e2c-97be-2f64d65069ea.jpg?1775937759"
    }
}
