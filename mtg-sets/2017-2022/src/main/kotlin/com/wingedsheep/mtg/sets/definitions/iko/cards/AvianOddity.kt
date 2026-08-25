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
 * Avian Oddity — Ikoria: Lair of Behemoths #42
 * {3}{U} · Creature — Bird · 2/4
 *
 * Flying
 * Cycling {2}{U}
 * When you cycle this card, put a flying counter on target creature you control.
 *
 * The cycling payoff is a *keyword counter* (CR 122.1e / 702.9), not an until-end-of-turn grant:
 * a `flying` counter stays on the creature for good, so the Oddity trades itself for a permanent
 * evasion upgrade plus the card cycling draws. [Triggers.YouCycleThis] fires from the discard and
 * resolves with the card already in the graveyard, so the target is chosen at that point.
 */
val AvianOddity = card("Avian Oddity") {
    manaCost = "{3}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Bird"
    power = 2
    toughness = 4
    oracleText = "Flying\n" +
        "Cycling {2}{U} ({2}{U}, Discard this card: Draw a card.)\n" +
        "When you cycle this card, put a flying counter on target creature you control."

    keywords(Keyword.FLYING)

    keywordAbility(KeywordAbility.cycling("{2}{U}"))

    triggeredAbility {
        trigger = Triggers.YouCycleThis
        val creature = target("target", Targets.CreatureYouControl)
        effect = Effects.AddCounters(Counters.FLYING, 1, creature)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "42"
        artist = "Simon Dominic"
        imageUri = "https://cards.scryfall.io/normal/front/f/3/f325873b-97de-4701-910f-ec5cdb66de33.jpg"
    }
}
