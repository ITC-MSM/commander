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
 * Titanoth Rex
 * {7}{G}{G}
 * Creature — Dinosaur Beast
 * 11/11
 *
 * Trample
 * Cycling {1}{G}
 * When you cycle this card, put a trample counter on target creature you control.
 *
 * The cycling payoff is a separate triggered ability from cycling itself (CR 702.29b): it goes on
 * the stack from the discard and resolves with the Rex already in the graveyard. A trample counter
 * is a keyword counter (CR 122.1b / 613.1f) — [Counters.TRAMPLE] is wired through the projector's
 * keyword-counter map, so the creature has trample for as long as the counter is on it, unlike the
 * end-of-turn grant a "gains trample" clause would give.
 */
val TitanothRex = card("Titanoth Rex") {
    manaCost = "{7}{G}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Dinosaur Beast"
    power = 11
    toughness = 11
    oracleText = "Trample\n" +
        "Cycling {1}{G} ({1}{G}, Discard this card: Draw a card.)\n" +
        "When you cycle this card, put a trample counter on target creature you control."

    keywords(Keyword.TRAMPLE)

    keywordAbility(KeywordAbility.cycling("{1}{G}"))

    triggeredAbility {
        trigger = Triggers.YouCycleThis
        val t = target("target", Targets.CreatureYouControl)
        effect = Effects.AddCounters(Counters.TRAMPLE, 1, t)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "174"
        artist = "Svetlin Velinov"
        imageUri = "https://cards.scryfall.io/normal/front/9/d/9d02e1e8-b85b-4e26-8ab8-ca2f49d05b88.jpg"
    }
}
