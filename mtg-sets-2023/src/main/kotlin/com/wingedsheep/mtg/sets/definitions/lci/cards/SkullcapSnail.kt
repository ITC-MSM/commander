package com.wingedsheep.mtg.sets.definitions.lci.cards

import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Skullcap Snail
 * {1}{B}
 * Creature — Fungus Snail
 * 1/1
 *
 * When this creature enters, target opponent exiles a card from their hand.
 *
 * The exile is chosen by the target opponent: `Patterns.Hand.exileFromHand`
 * derives the chooser from the effect target, so the opponent picks which card
 * of their own hand goes to exile (same primitive as Ruthless Negotiation).
 */
val SkullcapSnail = card("Skullcap Snail") {
    manaCost = "{1}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Fungus Snail"
    power = 1
    toughness = 1
    oracleText = "When this creature enters, target opponent exiles a card from their hand."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val opponent = target("target opponent", Targets.Opponent)
        effect = Patterns.Hand.exileFromHand(1, opponent)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "119"
        artist = "Maxime Minard"
        flavorText = "\"Oh, come on! Aren't regular snails gross enough?\"\n—Derilene, expedition porter"
        imageUri = "https://cards.scryfall.io/normal/front/0/d/0d96d019-5d80-468a-b891-e3e99346372a.jpg?1782694516"
    }
}
