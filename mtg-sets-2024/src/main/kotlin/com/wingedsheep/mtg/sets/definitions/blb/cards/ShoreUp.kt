package com.wingedsheep.mtg.sets.definitions.blb.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Shore Up
 * {U}
 * Instant
 *
 * Target creature you control gets +1/+1 and gains hexproof until end of turn.
 * Untap it.
 */
val ShoreUp = card("Shore Up") {
    manaCost = "{U}"
    colorIdentity = "U"
    typeLine = "Instant"
    oracleText = "Target creature you control gets +1/+1 and gains hexproof until end of turn. Untap it."

    spell {
        val creature = target("creature you control", Targets.CreatureYouControl)
        effect = Effects.ModifyStats(1, 1, creature)
            .then(Effects.GrantKeyword(Keyword.HEXPROOF, creature))
            .then(Effects.Untap(creature))
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "69"
        artist = "Raph Lomotan"
        flavorText = "\"Is that all you've got? Put some mussel into it!\""
        imageUri = "https://cards.scryfall.io/normal/front/4/d/4dc3b49e-3674-494c-bdea-4374cefd10f4.jpg?1721426233"
    }
}
