package com.wingedsheep.mtg.sets.definitions.mh1.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.MayEffect
import com.wingedsheep.sdk.scripting.effects.ShuffleLibraryEffect

/**
 * Pondering Mage
 * {3}{U}{U}
 * Creature — Human Wizard
 * 3/4
 * When this creature enters, look at the top three cards of your library, then put them back in any order. You may shuffle. Draw a card.
 *
 * Ponder stapled to a body, and it composes exactly Ponder's three parts: the look-and-reorder
 * pipeline, an optional shuffle, then the draw. The shuffle comes *after* the reorder, so saying
 * yes throws the chosen order away — see
 * [com.wingedsheep.mtg.sets.definitions.lrw.cards.Ponder].
 */
val PonderingMage = card("Pondering Mage") {
    manaCost = "{3}{U}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Human Wizard"
    power = 3
    toughness = 4
    oracleText = "When this creature enters, look at the top three cards of your library, then put them back in any order. You may shuffle. Draw a card."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.Composite(
            Patterns.Library.lookAtTopAndReorder(count = 3),
            MayEffect(ShuffleLibraryEffect()),
            Effects.DrawCards(1),
        )
        description = "When this creature enters, look at the top three cards of your library, then put them back in any order. You may shuffle. Draw a card."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "63"
        artist = "Tommy Arnold"
        flavorText = "\"Never leave the future to fate.\""
        imageUri = "https://cards.scryfall.io/normal/front/c/0/c08c5ac8-5c0b-4c89-8f06-e5aadfccee01.jpg?1783933139"
    }
}
