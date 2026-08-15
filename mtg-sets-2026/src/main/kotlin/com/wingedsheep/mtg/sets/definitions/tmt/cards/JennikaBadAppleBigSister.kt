package com.wingedsheep.mtg.sets.definitions.tmt.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.effects.CreateTokenEffect

/**
 * Jennika, Bad Apple Big Sister
 * {4}{W}
 * Legendary Creature — Mutant Ninja Turtle
 * 3/3
 *
 * When Jennika enters, create a 2/2 red Mutant creature token.
 * Plainscycling {2}
 */
val JennikaBadAppleBigSister = card("Jennika, Bad Apple Big Sister") {
    manaCost = "{4}{W}"
    colorIdentity = "W"
    typeLine = "Legendary Creature — Mutant Ninja Turtle"
    oracleText = "When Jennika enters, create a 2/2 red Mutant creature token.\nPlainscycling {2} ({2}, Discard this card: Search your library for a Plains card, reveal it, put it into your hand, then shuffle.)"
    power = 3
    toughness = 3

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = CreateTokenEffect(
            power = 2,
            toughness = 2,
            colors = setOf(Color.RED),
            creatureTypes = setOf("Mutant"),
            imageUri = "https://cards.scryfall.io/normal/front/5/1/51e33613-7a24-461c-8d9f-12680af4b92a.jpg?1771590526"
        )
    }

    keywordAbility(KeywordAbility.typecycling("Plains", "{2}"))

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "10"
        artist = "InHyuk Lee"
        flavorText = "\"Splinter said that, once, ninjas defended the poor and the weak from power. We could be real ninjas. We can fight for the people!\""
        imageUri = "https://cards.scryfall.io/normal/front/a/5/a5b83101-e3ef-4ffe-a886-4fc2b57a0947.jpg?1771502505"
    }
}
