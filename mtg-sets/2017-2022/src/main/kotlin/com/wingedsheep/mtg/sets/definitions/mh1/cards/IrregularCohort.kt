package com.wingedsheep.mtg.sets.definitions.mh1.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Irregular Cohort
 * {2}{W}{W}
 * Creature — Shapeshifter
 * 2/2
 * Changeling (This card is every creature type.)
 * When this creature enters, create a 2/2 colorless Shapeshifter creature token with changeling.
 *
 * Changeling is a real keyword the engine reads, so it stays a bare [Keyword.CHANGELING] on the card — and
 * the token repeats it in [Effects.CreateToken]'s `keywords`, since a token's copy of it is its own grant.
 */
val IrregularCohort = card("Irregular Cohort") {
    manaCost = "{2}{W}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Shapeshifter"
    power = 2
    toughness = 2
    oracleText = "Changeling (This card is every creature type.)\n" +
        "When this creature enters, create a 2/2 colorless Shapeshifter creature token with changeling."

    keywords(Keyword.CHANGELING)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.CreateToken(
            power = 2,
            toughness = 2,
            creatureTypes = setOf("Shapeshifter"),
            keywords = setOf(Keyword.CHANGELING),
        )
        description = "When this creature enters, create a 2/2 colorless Shapeshifter creature token with changeling."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "15"
        artist = "Steve Argyle"
        flavorText = "Where fickle form meets lasting loyalty."
        imageUri = "https://cards.scryfall.io/normal/front/9/2/92c46961-cf1a-4f20-83aa-3d256db2388f.jpg?1783933160"
    }
}
