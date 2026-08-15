package com.wingedsheep.mtg.sets.definitions.woe.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Water Wings
 * {1}{U}
 * Instant
 *
 * Until end of turn, target creature you control has base power and toughness 4/4 and gains flying
 * and hexproof.
 *
 * [Effects.SetBasePowerAndToughness] sets base P/T to 4/4 in Layer 7b (so later +N/+N counters and
 * pumps still apply on top), and two [Effects.GrantKeyword] grants confer flying and hexproof. All
 * three effects default to [com.wingedsheep.sdk.scripting.Duration.EndOfTurn], matching the oracle
 * "until end of turn".
 */
val WaterWings = card("Water Wings") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Instant"
    oracleText = "Until end of turn, target creature you control has base power and toughness 4/4 " +
        "and gains flying and hexproof. (It can't be the target of spells or abilities your " +
        "opponents control.)"

    spell {
        val creature = target("creature you control", Targets.CreatureYouControl)
        effect = Effects.SetBasePowerAndToughness(4, 4, creature)
            .then(Effects.GrantKeyword(Keyword.FLYING, creature))
            .then(Effects.GrantKeyword(Keyword.HEXPROOF, creature))
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "77"
        artist = "Arash Radkia"
        flavorText = "Luckily for the plummeting Johann, the hydroloft spell was one he had actually mastered."
        imageUri = "https://cards.scryfall.io/normal/front/4/e/4ea4993c-d1ba-4b33-955b-e0874fd2132f.jpg?1783915113"
    }
}
