package com.wingedsheep.mtg.sets.definitions.iko.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Raking Claws
 * {1}{R}
 * Instant
 * Target creature gains double strike until end of turn.
 * Cycling {2} ({2}, Discard this card: Draw a card.)
 */
val RakingClaws = card("Raking Claws") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Instant"
    oracleText = "Target creature gains double strike until end of turn.\nCycling {2} ({2}, Discard this card: Draw a card.)"

    spell {
        val t = target("target", Targets.Creature)
        effect = Effects.GrantKeyword(Keyword.DOUBLE_STRIKE, t)
    }

    keywordAbility(KeywordAbility.cycling("{2}"))

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "131"
        artist = "Slawomir Maniak"
        flavorText = "\"How many claws does a monster have? Exactly one more than you've accounted for.\" —Alux, Skysail defender"
        imageUri = "https://cards.scryfall.io/normal/front/6/e/6eb0d9a2-f9bb-4d8e-a1ca-896c42f8ad56.jpg?1783931044"
    }
}
