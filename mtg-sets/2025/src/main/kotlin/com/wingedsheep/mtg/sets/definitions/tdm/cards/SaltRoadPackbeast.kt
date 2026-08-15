package com.wingedsheep.mtg.sets.definitions.tdm.cards

import com.wingedsheep.sdk.core.CardType
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Salt Road Packbeast — Tarkir: Dragonstorm #23
 * {5}{W} · Creature — Beast · 4/3
 *
 * Affinity for creatures (This spell costs {1} less to cast for each creature you control.)
 * When this creature enters, draw a card.
 *
 * Affinity is modeled with the existing [KeywordAbility.Affinity] keyword (CardType
 * granularity); the cost calculator already reduces generic cost by the number of
 * permanents of the named type the caster controls.
 */
val SaltRoadPackbeast = card("Salt Road Packbeast") {
    manaCost = "{5}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Beast"
    power = 4
    toughness = 3
    oracleText = "Affinity for creatures (This spell costs {1} less to cast for each creature you control.)\n" +
        "When this creature enters, draw a card."

    keywordAbility(KeywordAbility.Affinity(CardType.CREATURE))

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.DrawCards(1)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "23"
        artist = "Ben Wootten"
        flavorText = "\"She huffs like she's Queen Hellkite of the Scour, but we've been together for many journeys and she's never let me down.\"\n—Mandakh, Mardu scout"
        imageUri = "https://cards.scryfall.io/normal/front/9/8/98d548c9-42bc-4155-8211-0aea801c3724.jpg?1763487067"
    }
}
