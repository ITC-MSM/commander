package com.wingedsheep.mtg.sets.definitions.one.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Ribskiff
 * {4}
 * Artifact — Vehicle
 * 4/4
 *
 * Toxic 2 (Players dealt combat damage by this creature also get two poison counters.)
 * When this Vehicle enters, draw a card.
 * Crew 3 (Tap any number of creatures you control with total power 3 or more: This Vehicle becomes an artifact creature until end of turn.)
 *
 * A Vehicle isn't a creature at rest, so the printed 4/4 sits on the card and only becomes
 * live once crew turns it into an artifact creature; the toxic 2 rides along on that
 * creature form. "When this Vehicle enters" is a plain SELF enters trigger.
 */
val Ribskiff = card("Ribskiff") {
    manaCost = "{4}"
    typeLine = "Artifact — Vehicle"
    power = 4
    toughness = 4
    oracleText = "Toxic 2 (Players dealt combat damage by this creature also get two poison counters.)\n" +
        "When this Vehicle enters, draw a card.\n" +
        "Crew 3 (Tap any number of creatures you control with total power 3 or more: This Vehicle becomes an artifact creature until end of turn.)"

    keywordAbility(KeywordAbility.Numeric(Keyword.TOXIC, 2))

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.DrawCards(1)
    }

    keywordAbility(KeywordAbility.crew(3))

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "240"
        artist = "José Parodi"
        imageUri = "https://cards.scryfall.io/normal/front/1/e/1ec8f984-5ed4-4b34-8b2a-a113cbba001d.jpg?1783917986"
    }
}
