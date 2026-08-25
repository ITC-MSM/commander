package com.wingedsheep.mtg.sets.definitions.stx.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Eyetwitch — Strixhaven: School of Mages #70 (canonical printing)
 * {B} · Creature — Eye Bat · 1/1
 *
 * Flying
 * When this creature dies, learn.
 *
 * A one-mana flying chump blocker that replaces itself: the dies trigger reads last-known
 * information (CR 603.6e), so it resolves with Eyetwitch already in the graveyard.
 *
 * `Learn` is [Patterns.Mechanic.learn] (CR 701.48).
 */
val Eyetwitch = card("Eyetwitch") {
    manaCost = "{B}"
    colorIdentity = "B"
    typeLine = "Creature — Eye Bat"
    power = 1
    toughness = 1
    oracleText = "Flying\n" +
        "When this creature dies, learn. (You may reveal a Lesson card you own from outside the " +
        "game and put it into your hand, or discard a card to draw a card.)"

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.Dies
        effect = Patterns.Mechanic.learn()
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "70"
        artist = "Karl Kopinski"
        flavorText = "Oculomancers see the ideal potion ingredient. The bats don't see it that way."
        imageUri = "https://cards.scryfall.io/normal/front/1/f/1f4d1bb6-cb8f-4d01-9879-0b3a0585cbf4.jpg?1783927368"
    }
}
