package com.wingedsheep.mtg.sets.definitions.kld.cards

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
        collectorNumber = "146"
        artist = "Anastasia Ovchinnikova"
        flavorText = "Those who move harmoniously with the flow of aether can channel the power of the natural world."
        imageUri = "https://cards.scryfall.io/normal/front/5/c/5c026c39-b09c-408a-844f-fb5eb785862a.jpg?1783937183"
    }
}
