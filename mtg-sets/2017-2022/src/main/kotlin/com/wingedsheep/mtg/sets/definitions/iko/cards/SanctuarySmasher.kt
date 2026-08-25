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
 * Sanctuary Smasher
 * {4}{R}{R}
 * Creature — Rhino Beast
 * 6/4
 * First strike
 * Cycling {2}{R} ({2}{R}, Discard this card: Draw a card.)
 * When you cycle this card, put a first strike counter on target creature you control.
 *
 * The cycling trigger (CR 702.29b) goes on the stack from the discard and resolves from the
 * graveyard, so the Smasher hands its own first strike to something already on the battlefield
 * without ever being cast. [Counters.FIRST_STRIKE] is a keyword counter (CR 122.1b / 613.1f), so
 * unlike a granted keyword it never expires.
 */
val SanctuarySmasher = card("Sanctuary Smasher") {
    manaCost = "{4}{R}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Rhino Beast"
    power = 6
    toughness = 4
    oracleText = "First strike\nCycling {2}{R} ({2}{R}, Discard this card: Draw a card.)\nWhen you cycle this card, put a first strike counter on target creature you control."

    keywords(Keyword.FIRST_STRIKE)

    keywordAbility(KeywordAbility.cycling("{2}{R}"))

    triggeredAbility {
        trigger = Triggers.YouCycleThis
        val creature = target("target", Targets.CreatureYouControl)
        effect = Effects.AddCounters(Counters.FIRST_STRIKE, 1, creature)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "135"
        artist = "Mathias Kollros"
        imageUri = "https://cards.scryfall.io/normal/front/c/c/cc634c10-42c5-4bdc-bc22-f862ae285492.jpg"
    }
}
