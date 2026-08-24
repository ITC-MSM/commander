package com.wingedsheep.mtg.sets.definitions.stx.cards

import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Professor of Symbology — Strixhaven: School of Mages #24 (canonical printing)
 * {1}{W} · Creature — Kor Cleric · 2/1
 *
 * When this creature enters, learn.
 *
 * The plainest Learn card in the set: a vanilla body whose whole text is the keyword action,
 * which makes it the reference shape for [Patterns.Mechanic.learn] on an ETB trigger.
 *
 * `Learn` is [Patterns.Mechanic.learn] (CR 701.48) — the discard is offered first and taking it
 * forecloses the Lesson, so this is *not* a choose-one.
 */
val ProfessorOfSymbology = card("Professor of Symbology") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Kor Cleric"
    power = 2
    toughness = 1
    oracleText = "When this creature enters, learn. (You may reveal a Lesson card you own from " +
        "outside the game and put it into your hand, or discard a card to draw a card.)"

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Patterns.Mechanic.learn()
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "24"
        artist = "Jason Rainville"
        flavorText = "\"A language isn't dead until we stop learning from it.\""
        imageUri = "https://cards.scryfall.io/normal/front/f/4/f427cf73-9f5e-4ef5-bc4f-29ffbfda9d57.jpg?1783927387"
    }
}
