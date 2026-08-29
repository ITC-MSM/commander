package com.wingedsheep.mtg.sets.definitions.thb.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Thundering Chariot
 * {4}
 * Artifact — Vehicle
 * 3/3
 *
 * First strike, trample, haste
 * Crew 1 (Tap any number of creatures you control with total power 1 or more: This Vehicle becomes
 * an artifact creature until end of turn.)
 *
 * The Sky Skiff shape: the three simple keywords on the keyword set, and crew as a parameterized
 * [KeywordAbility.crew] — the engine's `CrewEnumerator` and `CrewVehicleHandler` both resolve the
 * requirement by finding `KeywordAbility.Numeric(Keyword.CREW, n)` on the card definition, so the
 * bare `Keyword.CREW` marker alone would render the line and do nothing.
 *
 * A Vehicle's printed P/T sits on a non-creature type line; `power`/`toughness` still model it, and
 * the crew effect is what turns those numbers on. Haste is not redundant here: the Vehicle has been
 * under your control only since this turn, so crewing it the turn it enters would otherwise leave it
 * summoning-sick (CR 302.6).
 */
val ThunderingChariot = card("Thundering Chariot") {
    manaCost = "{4}"
    colorIdentity = ""
    typeLine = "Artifact — Vehicle"
    power = 3
    toughness = 3
    oracleText = "First strike, trample, haste\n" +
        "Crew 1 (Tap any number of creatures you control with total power 1 or more: " +
        "This Vehicle becomes an artifact creature until end of turn.)"

    keywords(Keyword.FIRST_STRIKE, Keyword.TRAMPLE, Keyword.HASTE)

    keywordAbility(KeywordAbility.crew(1))

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "239"
        artist = "Aaron Miller"
        flavorText = "In times of conflict, the philosophers of Meletis trade their podiums for conveyances of war."
        imageUri = "https://cards.scryfall.io/normal/front/c/d/cd2fa92d-5521-421c-b3f8-7c14bbef3080.jpg"
    }
}
