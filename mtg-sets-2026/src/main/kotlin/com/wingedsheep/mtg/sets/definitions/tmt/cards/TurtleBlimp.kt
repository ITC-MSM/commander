package com.wingedsheep.mtg.sets.definitions.tmt.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.effects.CreateTokenEffect

/**
 * Turtle Blimp
 * {5}
 * Artifact — Vehicle
 * 3/4
 *
 * Flying
 * When this Vehicle enters, create a 2/2 red Mutant creature token.
 * Crew 2
 *
 * Mirrors the DOM Weatherlight / BLC Rolling Hamsphere Vehicle shape —
 * `typeLine = "Artifact — Vehicle"` plus
 * `keywordAbility(KeywordAbility.Numeric(Keyword.CREW, 2))` already
 * carry the full Vehicle pipeline (the artifact-becomes-artifact-
 * creature-until-EOT behaviour and the Crew activation).
 */
val TurtleBlimp = card("Turtle Blimp") {
    manaCost = "{5}"
    typeLine = "Artifact — Vehicle"
    oracleText = "Flying\nWhen this Vehicle enters, create a 2/2 red Mutant creature token.\nCrew 2 (Tap any number of creatures you control with total power 2 or more: This Vehicle becomes an artifact creature until end of turn.)"
    power = 3
    toughness = 4

    keywords(Keyword.FLYING)

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

    keywordAbility(KeywordAbility.Numeric(Keyword.CREW, 2))

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "180"
        artist = "Jakob Eirich"
        imageUri = "https://cards.scryfall.io/normal/front/6/e/6e118fc5-b0ae-4592-8292-d611856ae203.jpg?1771587078"
    }
}
