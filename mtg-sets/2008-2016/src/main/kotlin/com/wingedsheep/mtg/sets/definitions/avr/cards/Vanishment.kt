package com.wingedsheep.mtg.sets.definitions.avr.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Vanishment
 * {4}{U}
 * Instant
 *
 * Put target nonland permanent on top of its owner's library.
 * Miracle {U} (You may cast this card for its miracle cost when you draw it if it's the first card you drew this turn.)
 *
 * [Effects.PutOnTopOfLibrary] is the plain move to the top of the library — "its owner's" is not a
 * knob, since a permanent always leaves the battlefield for its owner's zone (CR 400.3). Set Adrift
 * (KTK) is the same shape without the miracle cost.
 */
val Vanishment = card("Vanishment") {
    manaCost = "{4}{U}"
    colorIdentity = "U"
    typeLine = "Instant"
    oracleText = "Put target nonland permanent on top of its owner's library.\n" +
        "Miracle {U} (You may cast this card for its miracle cost when you draw it if it's the first " +
        "card you drew this turn.)"

    spell {
        val permanent = target("target", Targets.NonlandPermanent)
        effect = Effects.PutOnTopOfLibrary(permanent)
    }

    keywordAbility(KeywordAbility.miracle("{U}"))

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "82"
        artist = "Daarken"
        imageUri = "https://cards.scryfall.io/normal/front/d/e/dece40c1-790c-4471-a790-1d356b345603.jpg?1783940709"
    }
}
