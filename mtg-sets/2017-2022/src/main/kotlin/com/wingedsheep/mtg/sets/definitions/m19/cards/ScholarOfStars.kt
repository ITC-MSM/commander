package com.wingedsheep.mtg.sets.definitions.m19.cards

import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Scholar of Stars
 * {3}{U}
 * Creature — Human Artificer
 * 3/2
 * When this creature enters, if you control an artifact, draw a card.
 *
 * "If you control an artifact" is an intervening-if clause (CR 603.4): it is checked when the
 * ability would trigger *and* again on resolution, so losing the artifact in response fizzles it.
 */
val ScholarOfStars = card("Scholar of Stars") {
    manaCost = "{3}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Human Artificer"
    power = 3
    toughness = 2
    oracleText = "When this creature enters, if you control an artifact, draw a card."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        interveningIf = Conditions.ControlArtifact
        effect = Effects.DrawCards(1)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "71"
        artist = "Tommy Arnold"
        flavorText = "\"The path of the stars is as reliable as the instruments that measure them.\""
        imageUri = "https://cards.scryfall.io/normal/front/c/b/cb4664d4-fb00-4572-a60d-00336117b8a5.jpg"
    }
}
