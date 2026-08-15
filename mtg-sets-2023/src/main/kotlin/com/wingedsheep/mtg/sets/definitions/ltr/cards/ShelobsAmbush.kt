package com.wingedsheep.mtg.sets.definitions.ltr.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Shelob's Ambush
 * {B}
 * Instant
 *
 * Target creature gets +1/+2 and gains deathtouch until end of turn. Create a Food token.
 */
val ShelobsAmbush = card("Shelob's Ambush") {
    manaCost = "{B}"
    colorIdentity = "B"
    typeLine = "Instant"
    oracleText = "Target creature gets +1/+2 and gains deathtouch until end of turn. Create a Food token. (It's an artifact with \"{2}, {T}, Sacrifice this token: You gain 3 life.\")"

    spell {
        val creature = target("target creature", Targets.Creature)
        effect = Effects.ModifyStats(1, 2, creature)
            .then(Effects.GrantKeyword(Keyword.DEATHTOUCH, creature))
            .then(Effects.CreateFood(1))
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "108"
        artist = "Simon Dominic"
        flavorText = "As Frodo ran forward, eager, rejoicing to be free, Shelob with hideous speed came behind and stung him in the neck."
        imageUri = "https://cards.scryfall.io/normal/front/2/5/25e69fa8-7339-4bfa-8e35-9cbfe0001d8b.jpg?1686968721"
    }
}
