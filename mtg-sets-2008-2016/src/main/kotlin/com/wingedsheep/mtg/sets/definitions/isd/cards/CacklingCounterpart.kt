package com.wingedsheep.mtg.sets.definitions.isd.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Cackling Counterpart
 * {1}{U}{U}
 * Instant
 * Create a token that's a copy of target creature you control.
 * Flashback {5}{U}{U} (You may cast this card from your graveyard for its flashback cost. Then exile it.)
 */
val CacklingCounterpart = card("Cackling Counterpart") {
    manaCost = "{1}{U}{U}"
    colorIdentity = "U"
    typeLine = "Instant"
    oracleText = "Create a token that's a copy of target creature you control.\n" +
        "Flashback {5}{U}{U} (You may cast this card from your graveyard for its flashback cost. Then exile it.)"

    spell {
        val t = target("target creature you control", Targets.CreatureYouControl)
        effect = Effects.CreateTokenCopyOfTarget(t)
    }
    keywordAbility(KeywordAbility.flashback("{5}{U}{U}"))

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "46"
        artist = "David Rapoza"
        imageUri = "https://cards.scryfall.io/normal/front/8/a/8a2a2b93-94dc-4285-a6fd-455a796426bc.jpg?1782714807"
    }
}
