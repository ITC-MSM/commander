package com.wingedsheep.mtg.sets.definitions.akh.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Stir the Sands
 * {4}{B}{B}
 * Sorcery
 * Create three 2/2 black Zombie creature tokens.
 * Cycling {3}{B} ({3}{B}, Discard this card: Draw a card.)
 * When you cycle this card, create a 2/2 black Zombie creature token.
 *
 * Three parts: the `spell { }` body, [KeywordAbility.cycling], and a [Triggers.YouCycleThis]
 * triggered ability that fires from the graveyard once the cycling ability has resolved.
 */
val StirTheSands = card("Stir the Sands") {
    manaCost = "{4}{B}{B}"
    colorIdentity = "B"
    typeLine = "Sorcery"
    oracleText = "Create three 2/2 black Zombie creature tokens.\n" +
            "Cycling {3}{B} ({3}{B}, Discard this card: Draw a card.)\n" +
            "When you cycle this card, create a 2/2 black Zombie creature token."

    spell {
        effect = Effects.CreateToken(
            power = 2,
            toughness = 2,
            colors = setOf(Color.BLACK),
            creatureTypes = setOf("Zombie"),
            count = 3,
            imageUri = "https://cards.scryfall.io/normal/front/b/5/b5bd6905-79be-4d2c-a343-f6e6a181b3e6.jpg?1783936411"
        )
    }

    keywordAbility(KeywordAbility.cycling("{3}{B}"))

    triggeredAbility {
        trigger = Triggers.YouCycleThis
        effect = Effects.CreateToken(
            power = 2,
            toughness = 2,
            colors = setOf(Color.BLACK),
            creatureTypes = setOf("Zombie"),
            imageUri = "https://cards.scryfall.io/normal/front/b/5/b5bd6905-79be-4d2c-a343-f6e6a181b3e6.jpg?1783936411"
        )
        description = "When you cycle this card, create a 2/2 black Zombie creature token."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "110"
        artist = "David Gaillet"
        imageUri = "https://cards.scryfall.io/normal/front/0/c/0cb84193-9648-46e5-a34d-c12cc3f1d7f2.jpg?1783936499"
    }
}
