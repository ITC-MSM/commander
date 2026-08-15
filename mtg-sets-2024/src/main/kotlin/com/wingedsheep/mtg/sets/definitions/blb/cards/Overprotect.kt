package com.wingedsheep.mtg.sets.definitions.blb.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Overprotect {1}{G}
 * Instant
 *
 * Target creature you control gets +3/+3 and gains trample, hexproof,
 * and indestructible until end of turn.
 */
val Overprotect = card("Overprotect") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Instant"
    oracleText = "Target creature you control gets +3/+3 and gains trample, hexproof, and indestructible until end of turn."

    spell {
        val creature = target("target creature you control", Targets.CreatureYouControl)
        effect = Effects.ModifyStats(3, 3, creature)
            .then(Effects.GrantKeyword(Keyword.TRAMPLE, creature))
            .then(Effects.GrantKeyword(Keyword.HEXPROOF, creature))
            .then(Effects.GrantKeyword(Keyword.INDESTRUCTIBLE, creature))
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "185"
        artist = "Pavel Kolomeyets"
        flavorText = "\"Blor's defenses are impervious!\"\n—Blor the Impervious"
        imageUri = "https://cards.scryfall.io/normal/front/0/7/079e979f-b618-4625-989c-e0ea5b61ed8a.jpg?1721426880"
    }
}
