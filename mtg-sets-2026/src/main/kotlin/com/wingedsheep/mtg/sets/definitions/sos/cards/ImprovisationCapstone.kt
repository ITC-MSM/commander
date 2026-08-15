package com.wingedsheep.mtg.sets.definitions.sos.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.GrantMayPlayFromExileEffect
import com.wingedsheep.sdk.scripting.effects.GrantPlayWithoutPayingCostEffect
import com.wingedsheep.sdk.scripting.references.Player

/**
 * Improvisation Capstone
 * {5}{R}{R}
 * Sorcery — Lesson
 *
 * Exile cards from the top of your library until you exile cards with total mana value 4 or
 * greater. You may cast any number of spells from among them without paying their mana costs.
 * Paradigm (Then exile this spell. After you first resolve a spell with this name, you may cast a
 * copy of it from exile without paying its mana cost at the beginning of each of your first main
 * phases.)
 *
 * Same impulse-and-free-cast shape as Dream Harvest, but on your own library: exile from the top
 * until total mana value reaches 4 ([Effects.ExileLibraryUntilManaValue]), then grant free-cast
 * permission for end of turn ([GrantMayPlayFromExileEffect] + [GrantPlayWithoutPayingCostEffect]).
 * `paradigm()` exiles the spell on resolution and synthesizes the recurring free copy.
 */
val ImprovisationCapstone = card("Improvisation Capstone") {
    manaCost = "{5}{R}{R}"
    colorIdentity = "R"
    typeLine = "Sorcery — Lesson"
    oracleText = "Exile cards from the top of your library until you exile cards with total mana " +
        "value 4 or greater. You may cast any number of spells from among them without paying " +
        "their mana costs.\n" +
        "Paradigm (Then exile this spell. After you first resolve a spell with this name, you may " +
        "cast a copy of it from exile without paying its mana cost at the beginning of each of " +
        "your first main phases.)"

    spell {
        effect = Effects.Composite(
            listOf(
                Effects.ExileLibraryUntilManaValue(
                    players = Player.You,
                    threshold = 4,
                    storeAs = "exiled",
                ),
                GrantMayPlayFromExileEffect("exiled"),
                GrantPlayWithoutPayingCostEffect("exiled"),
            ),
        )
        paradigm()
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "120"
        artist = "Marta Nael"
        imageUri = "https://cards.scryfall.io/normal/front/d/0/d01fe6e9-49ee-4708-833e-75cd5a9f167c.jpg?1775937787"
    }
}
