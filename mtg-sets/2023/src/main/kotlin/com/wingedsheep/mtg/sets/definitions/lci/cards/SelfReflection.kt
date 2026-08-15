package com.wingedsheep.mtg.sets.definitions.lci.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Self-Reflection
 * {4}{U}{U}
 * Sorcery
 * Create a token that's a copy of target creature you control.
 * Flashback {3}{U} (You may cast this card from your graveyard for its flashback cost. Then exile it.)
 *
 * Sorcery-speed sibling of [com.wingedsheep.mtg.sets.definitions.isd.cards.CacklingCounterpart].
 */
val SelfReflection = card("Self-Reflection") {
    manaCost = "{4}{U}{U}"
    colorIdentity = "U"
    typeLine = "Sorcery"
    oracleText = "Create a token that's a copy of target creature you control.\n" +
        "Flashback {3}{U} (You may cast this card from your graveyard for its flashback cost. Then exile it.)"

    spell {
        val t = target("target creature you control", Targets.CreatureYouControl)
        effect = Effects.CreateTokenCopyOfTarget(t)
    }
    keywordAbility(KeywordAbility.flashback("{3}{U}"))

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "74"
        artist = "Henry Peters"
        flavorText = "Some Echoes remember every moment of their past lives and spend their existence " +
            "contemplating past choices."
        imageUri = "https://cards.scryfall.io/normal/front/1/2/1242203d-c9b5-4ab6-802e-e222f92291e9.jpg?1782694551"
    }
}
