package com.wingedsheep.mtg.sets.definitions.drk.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Pikemen
 * {1}{W}
 * Creature — Human Soldier
 * 1/1
 *
 * First strike; banding — both fully handled by the combat engine (CR 702.7 and CR 702.22),
 * so the card only declares them.
 */
val Pikemen = card("Pikemen") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Soldier"
    power = 1
    toughness = 1
    oracleText = "First strike; banding (Any creatures with banding, and up to one without, " +
        "can attack in a band. Bands are blocked as a group. If any creatures with banding you " +
        "control are blocking or being blocked by a creature, you divide that creature's combat " +
        "damage, not its controller, among any of the creatures it's being blocked by or is " +
        "blocking.)"

    keywords(Keyword.FIRST_STRIKE, Keyword.BANDING)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "15"
        artist = "Dennis Detwiller"
        flavorText = "\"As the cavalry bore down, we faced them with swords drawn and pikes hidden in the grass at our feet. 'Don't lift your pikes 'til I give the word,' I said.\" —Maeveen O'Donagh, *Memoirs of a Soldier*"
        imageUri = "https://cards.scryfall.io/normal/front/b/f/bf2f6936-b50c-4907-9b55-ebf8a3fba8f5.jpg?1783947946"
    }
}
