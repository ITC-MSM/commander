package com.wingedsheep.mtg.sets.definitions.iko.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Void Beckoner
 * {6}{B}{B}
 * Creature — Nightmare Horror
 * 8/8
 *
 * Deathtouch
 * Cycling {2}{B}
 * When you cycle this card, put a deathtouch counter on target creature you control.
 *
 * The black half of Ikoria's keyword-counter cycling cycle (see Titanoth Rex). The payoff is a
 * separate triggered ability from cycling itself (CR 702.29b) and resolves with the Beckoner
 * already in the graveyard; a deathtouch counter is a keyword counter (CR 122.1b / 613.1f), so the
 * creature keeps deathtouch for as long as the counter is on it rather than only until end of turn.
 */
val VoidBeckoner = card("Void Beckoner") {
    manaCost = "{6}{B}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Nightmare Horror"
    power = 8
    toughness = 8
    oracleText = "Deathtouch\n" +
        "Cycling {2}{B} ({2}{B}, Discard this card: Draw a card.)\n" +
        "When you cycle this card, put a deathtouch counter on target creature you control."

    keywords(Keyword.DEATHTOUCH)

    keywordAbility(KeywordAbility.cycling("{2}{B}"))

    triggeredAbility {
        trigger = Triggers.YouCycleThis
        val t = target("target", Targets.CreatureYouControl)
        effect = Effects.AddCounters(Counters.DEATHTOUCH, 1, t)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "104"
        artist = "Daarken"
        imageUri = "https://cards.scryfall.io/normal/front/a/1/a1523cda-c47d-4419-a5d3-fd6ed9867c56.jpg"
    }
}
