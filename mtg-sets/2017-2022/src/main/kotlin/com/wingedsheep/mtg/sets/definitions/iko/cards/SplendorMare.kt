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
 * Splendor Mare
 * {2}{W}
 * Creature — Elk Unicorn
 * 3/3
 *
 * Lifelink
 * Cycling {1}{W} ({1}{W}, Discard this card: Draw a card.)
 * When you cycle this card, put a lifelink counter on target creature you control.
 *
 * The cycling trigger (CR 702.29b) goes on the stack from the discard and resolves from the
 * graveyard, so the Mare hands its lifelink to something already on the battlefield without ever
 * being cast. A lifelink counter is a keyword counter (CR 122.1b / 613.1f) — the projection maps
 * it to the keyword, so the grant lasts as long as the counter does.
 */
val SplendorMare = card("Splendor Mare") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Elk Unicorn"
    power = 3
    toughness = 3
    oracleText = "Lifelink\n" +
        "Cycling {1}{W} ({1}{W}, Discard this card: Draw a card.)\n" +
        "When you cycle this card, put a lifelink counter on target creature you control."

    keywords(Keyword.LIFELINK)

    keywordAbility(KeywordAbility.cycling("{1}{W}"))

    triggeredAbility {
        trigger = Triggers.YouCycleThis
        val t = target("target", Targets.CreatureYouControl)
        effect = Effects.AddCounters(Counters.LIFELINK, 1, t)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "32"
        artist = "Lucas Graciano"
        imageUri = "https://cards.scryfall.io/normal/front/5/3/535ccc75-b24e-4071-b6a0-fc5267a454e3.jpg"
    }
}
