package com.wingedsheep.mtg.sets.definitions.ecl.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Blossoming Defense
 * {G}
 * Instant
 *
 * Target creature you control gets +2/+2 and gains hexproof until end of turn.
 */
val BlossomingDefense = card("Blossoming Defense") {
    manaCost = "{G}"
    colorIdentity = "G"
    typeLine = "Instant"
    oracleText = "Target creature you control gets +2/+2 and gains hexproof until end of turn. (It can't be the target of spells or abilities your opponents control.)"

    spell {
        val creature = target("creature you control", Targets.CreatureYouControl)
        effect = Effects.ModifyStats(2, 2, creature)
            .then(Effects.GrantKeyword(Keyword.HEXPROOF, creature))
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "167"
        artist = "Eelis Kyttanen"
        flavorText = "To survive Shadowmoor's wilds, safewrights must carry an unassailable seed of beauty within their heart."
        imageUri = "https://cards.scryfall.io/normal/front/3/c/3cce80d0-b937-4325-a603-1278c110f244.jpg?1767658356"
    }
}
