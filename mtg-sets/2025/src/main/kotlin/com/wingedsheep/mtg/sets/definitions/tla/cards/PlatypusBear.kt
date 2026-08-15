package com.wingedsheep.mtg.sets.definitions.tla.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CanAttackDespiteDefender

/**
 * Platypus-Bear
 * {1}{G/U}
 * Creature — Platypus Bear
 * 2/3
 * Defender
 * When this creature enters, mill two cards. (Put the top two cards of your library into your graveyard.)
 * As long as there is a Lesson card in your graveyard, this creature can attack as though it didn't
 * have defender.
 */
val PlatypusBear = card("Platypus-Bear") {
    manaCost = "{1}{G/U}"
    colorIdentity = "GU"
    typeLine = "Creature — Platypus Bear"
    power = 2
    toughness = 3
    oracleText = "Defender\n" +
        "When this creature enters, mill two cards. (Put the top two cards of your library into your graveyard.)\n" +
        "As long as there is a Lesson card in your graveyard, this creature can attack as though it didn't have defender."

    keywords(Keyword.DEFENDER)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Patterns.Library.mill(2)
    }

    staticAbility {
        ability = CanAttackDespiteDefender(
            condition = Conditions.GraveyardContainsSubtype(Subtype.LESSON)
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "236"
        artist = "Maël Ollivier-Henry"
        imageUri = "https://cards.scryfall.io/normal/front/7/e/7ebf742a-3995-4b22-9dc7-b05f57889683.jpg?1764121750"
    }
}
