package com.wingedsheep.mtg.sets.definitions.stx.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Gnarled Professor — Strixhaven: School of Mages #133 (canonical printing)
 * {2}{G}{G} · Creature — Treefolk Druid · 5/4
 *
 * Trample
 * When this creature enters, learn.
 *
 * A four-mana 5/4 trample that also fetches a Lesson — the green end of the Learn cycle, and the
 * same ETB shape as Professor of Symbology on a much bigger body.
 *
 * `Learn` is [Patterns.Mechanic.learn] (CR 701.48).
 */
val GnarledProfessor = card("Gnarled Professor") {
    manaCost = "{2}{G}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Treefolk Druid"
    power = 5
    toughness = 4
    oracleText = "Trample\n" +
        "When this creature enters, learn. (You may reveal a Lesson card you own from outside the " +
        "game and put it into your hand, or discard a card to draw a card.)"

    keywords(Keyword.TRAMPLE)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Patterns.Mechanic.learn()
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "133"
        artist = "Simon Dominic"
        flavorText = "\"Class is in season.\""
        imageUri = "https://cards.scryfall.io/normal/front/a/3/a32338e8-1f6a-49b9-bd93-26578adab6b3.jpg?1783927342"
    }
}
