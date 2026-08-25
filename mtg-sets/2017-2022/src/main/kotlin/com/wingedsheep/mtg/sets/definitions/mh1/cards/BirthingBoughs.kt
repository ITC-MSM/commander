package com.wingedsheep.mtg.sets.definitions.mh1.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Birthing Boughs
 * {3}
 * Artifact
 * {4}, {T}: Create a 2/2 colorless Shapeshifter creature token with changeling. (It is every creature type.)
 *
 * The Boughs itself has no changeling — only the token does, and a token's copy of the keyword is
 * its own grant, so [Keyword.CHANGELING] rides [Effects.CreateToken]'s `keywords` rather than the
 * card. Same token as Irregular Cohort's; see
 * [com.wingedsheep.mtg.sets.definitions.mh1.cards.IrregularCohort].
 */
val BirthingBoughs = card("Birthing Boughs") {
    manaCost = "{3}"
    colorIdentity = ""
    typeLine = "Artifact"
    oracleText = "{4}, {T}: Create a 2/2 colorless Shapeshifter creature token with changeling. (It is every creature type.)"

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{4}"), Costs.Tap)
        effect = Effects.CreateToken(
            power = 2,
            toughness = 2,
            creatureTypes = setOf("Shapeshifter"),
            keywords = setOf(Keyword.CHANGELING),
        )
        description = "{4}, {T}: Create a 2/2 colorless Shapeshifter creature token with changeling."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "221"
        artist = "Mike Bierek"
        flavorText = "Changelings can't remember where they came from. They just know it wasn't a cradle."
        imageUri = "https://cards.scryfall.io/normal/front/1/3/1345ee24-ffa3-44d1-a983-f25f54cda3f3.jpg?1783933075"
    }
}
