package com.wingedsheep.mtg.sets.definitions.tsp.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Thrill of the Hunt
 * {G}
 * Instant
 *
 * Target creature gets +1/+2 until end of turn.
 * Flashback {W} (You may cast this card from your graveyard for its flashback cost. Then exile it.)
 */
val ThrillOfTheHunt = card("Thrill of the Hunt") {
    manaCost = "{G}"
    colorIdentity = "GW"
    typeLine = "Instant"
    oracleText = "Target creature gets +1/+2 until end of turn.\n" +
        "Flashback {W} (You may cast this card from your graveyard for its flashback cost. Then exile it.)"

    spell {
        val t = target("target", Targets.Creature)
        effect = Effects.ModifyStats(1, 2, t)
    }

    keywordAbility(KeywordAbility.flashback("{W}"))

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "229"
        artist = "Stephen Tappin"
        imageUri = "https://cards.scryfall.io/normal/front/e/8/e8fe0c8e-f361-4eac-8c2c-ca6602dad352.jpg"
    }
}
