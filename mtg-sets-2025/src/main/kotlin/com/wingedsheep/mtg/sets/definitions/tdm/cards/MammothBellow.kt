package com.wingedsheep.mtg.sets.definitions.tdm.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Mammoth Bellow — Tarkir: Dragonstorm #205
 * {2}{G}{U}{R} · Sorcery
 *
 * Create a 5/5 green Elephant creature token.
 * Harmonize {5}{G}{U}{R} (You may cast this card from your graveyard for its harmonize
 * cost. You may tap a creature you control to reduce that cost by {X}, where X is its
 * power. Then exile this spell.)
 */
val MammothBellow = card("Mammoth Bellow") {
    manaCost = "{2}{G}{U}{R}"
    colorIdentity = "GUR"
    typeLine = "Sorcery"
    oracleText = "Create a 5/5 green Elephant creature token.\n" +
        "Harmonize {5}{G}{U}{R} (You may cast this card from your graveyard for its harmonize cost. " +
        "You may tap a creature you control to reduce that cost by {X}, where X is its power. Then exile this spell.)"

    spell {
        effect = Effects.CreateToken(
            power = 5,
            toughness = 5,
            colors = setOf(Color.GREEN),
            creatureTypes = setOf("Elephant")
        )
    }

    keywordAbility(KeywordAbility.harmonize("{5}{G}{U}{R}"))

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "205"
        artist = "Xavier Ribeiro"
        imageUri = "https://cards.scryfall.io/normal/front/4/6/468b17b4-79ce-4dfa-8873-a9cfc347e38f.jpg?1743204808"
    }
}
