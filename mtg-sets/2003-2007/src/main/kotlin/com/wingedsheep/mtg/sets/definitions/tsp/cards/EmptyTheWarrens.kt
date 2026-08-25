package com.wingedsheep.mtg.sets.definitions.tsp.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Empty the Warrens
 * {3}{R}
 * Sorcery
 * Create two 1/1 red Goblin creature tokens.
 * Storm (When you cast this spell, copy it for each spell cast before it this turn.)
 *
 * Storm (CR 702.40) is engine-live off the printed keyword, but the copy path reads
 * `script.spellEffect`: the card must stay a plain `spell { effect = … }`, never a modal or
 * replacement shape, or the storm trigger resolves into zero copies.
 *
 * The Goblin token carries no `imageUri` — Time Spiral's own token art resolves through
 * `TokenArtData.forSet`, so hard-coding a URI here would only pin the wrong printing.
 */
val EmptyTheWarrens = card("Empty the Warrens") {
    manaCost = "{3}{R}"
    colorIdentity = "R"
    typeLine = "Sorcery"
    oracleText = "Create two 1/1 red Goblin creature tokens.\n" +
        "Storm (When you cast this spell, copy it for each spell cast before it this turn.)"

    spell {
        effect = Effects.CreateToken(
            power = 1,
            toughness = 1,
            colors = setOf(Color.RED),
            creatureTypes = setOf("Goblin"),
            count = 2
        )
    }

    keywords(Keyword.STORM)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "152"
        artist = "Mark Brill"
        flavorText = "\"They'd pour out of the warrens to make war (and to make room for the littering matrons).\"\n—*Sarpadian Empires, vol. IV*"
        imageUri = "https://cards.scryfall.io/normal/front/9/5/952bb27c-c58a-478a-b637-eb4f7e1e0ab4.jpg"
    }
}
