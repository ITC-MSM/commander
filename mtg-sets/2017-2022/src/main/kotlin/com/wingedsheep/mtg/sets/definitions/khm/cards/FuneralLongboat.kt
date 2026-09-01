package com.wingedsheep.mtg.sets.definitions.khm.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Funeral Longboat
 * {2}
 * Artifact — Vehicle
 * 3/3
 * Vigilance
 * Crew 1 (Tap any number of creatures you control with total power 1 or more: This Vehicle becomes an artifact creature until end of turn.)
 *
 * A Vehicle: not a creature until crewed. Crew is a real ability the engine reads, so it is lowered
 * into [KeywordAbility.crew] rather than left as the display-only `Keyword.CREW` — the card builder
 * derives the printed keyword back out of the ability.
 */
val FuneralLongboat = card("Funeral Longboat") {
    manaCost = "{2}"
    colorIdentity = ""
    typeLine = "Artifact — Vehicle"
    oracleText = "Vigilance\n" +
        "Crew 1 (Tap any number of creatures you control with total power 1 or more: This Vehicle becomes an artifact creature until end of turn.)"
    power = 3
    toughness = 3

    keywords(Keyword.VIGILANCE)

    keywordAbility(KeywordAbility.crew(1))

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "238"
        artist = "Donato Giancola"
        flavorText = "\"Cast onto the windless water, he drifted until the sea turned to sky.\"\n" +
            "—*Saga of the Lost King*"
        imageUri = "https://cards.scryfall.io/normal/front/4/5/45c48042-178f-432e-9eee-10bfa1e0795f.jpg"
    }
}
