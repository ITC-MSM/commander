package com.wingedsheep.mtg.sets.definitions.bro.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Whirling Strike
 * {1}{R}
 * Instant
 * Target creature gets +2/+0 and gains first strike and trample until end of turn.
 */
val WhirlingStrike = card("Whirling Strike") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Instant"
    oracleText = "Target creature gets +2/+0 and gains first strike and trample until end of turn."

    spell {
        val t = target("target", Targets.Creature)
        effect = Effects.Composite(
            Effects.ModifyStats(2, 0, t),
            Effects.GrantKeyword(Keyword.FIRST_STRIKE, t),
            Effects.GrantKeyword(Keyword.TRAMPLE, t)
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "157"
        artist = "Aaron J. Riley"
        flavorText = "\"I will trust desert steel in Fallaji hands over a soulless machine any day.\"\n—Hajar, Mishra's bodyguard"
        imageUri = "https://cards.scryfall.io/normal/front/b/1/b17950ae-43aa-4d03-a41e-726ca96eb1ba.jpg?1783920058"
    }
}
