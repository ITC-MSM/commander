package com.wingedsheep.mtg.sets.definitions.ecl.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.MayPlayExpiry

/**
 * Sizzling Changeling
 * {2}{R}
 * Creature — Shapeshifter
 * 3/2
 *
 * Changeling (This card is every creature type.)
 * When this creature dies, exile the top card of your library. Until the end of your
 * next turn, you may play that card.
 */
val SizzlingChangeling = card("Sizzling Changeling") {
    manaCost = "{2}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Shapeshifter"
    power = 3
    toughness = 2
    oracleText = "Changeling (This card is every creature type.)\n" +
        "When this creature dies, exile the top card of your library. Until the end of your next turn, you may play that card."

    keywords(Keyword.CHANGELING)

    triggeredAbility {
        trigger = Triggers.Dies
        effect = Patterns.Exile.impulse(count = 1, expiry = MayPlayExpiry.UntilEndOfNextTurn)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "155"
        artist = "Chris Seaman"
        flavorText = "Burrenton's forges call to it, and it arrives whenever they need to be relit."
        imageUri = "https://cards.scryfall.io/normal/front/e/5/e58f0722-9ad1-4952-9aee-ea8137c58911.jpg?1767658331"
    }
}
