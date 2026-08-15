package com.wingedsheep.mtg.sets.definitions.dmu.cards

import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.MayEffect
import com.wingedsheep.sdk.scripting.effects.SearchDestination

/**
 * Micromancer
 * {3}{U}
 * Creature — Human Wizard
 * 3/3
 * When this creature enters, you may search your library for an instant or sorcery card with
 * mana value 1, reveal it, put it into your hand, then shuffle.
 *
 * Canonical printing (Dominaria United); Foundations reprint is a Printing row in the fdn package.
 */
val Micromancer = card("Micromancer") {
    manaCost = "{3}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Human Wizard"
    power = 3
    toughness = 3
    oracleText = "When this creature enters, you may search your library for an instant or sorcery card " +
        "with mana value 1, reveal it, put it into your hand, then shuffle."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = MayEffect(
            Patterns.Library.searchLibrary(
                filter = GameObjectFilter.InstantOrSorcery.manaValue(1),
                count = 1,
                destination = SearchDestination.HAND,
                reveal = true,
                shuffleAfter = true
            )
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "57"
        artist = "Ernanda Souza"
        flavorText = "\"Ideas don't have to be big to be great.\""
        imageUri = "https://cards.scryfall.io/normal/front/b/2/b21203c8-a935-4ce0-a742-148587e32145.jpg?1782700520"
    }
}
