package com.wingedsheep.mtg.sets.definitions.kld.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Aradara Express
 * {5}
 * Artifact — Vehicle
 * 8/6
 * Menace
 * Crew 4 (Tap any number of creatures you control with total power 4 or more: This Vehicle becomes an artifact creature until end of turn.)
 *
 * Both halves are printed keywords: [Keyword.MENACE] and the [KeywordAbility.crew] activated
 * ability — the engine owns the crew tap-for-total-power pipeline, so nothing card-specific is
 * needed. Menace only matters once the Vehicle is crewed and attacking.
 */
val AradaraExpress = card("Aradara Express") {
    manaCost = "{5}"
    colorIdentity = ""
    typeLine = "Artifact — Vehicle"
    power = 8
    toughness = 6
    oracleText = "Menace\nCrew 4 (Tap any number of creatures you control with total power 4 or more: This Vehicle becomes an artifact creature until end of turn.)"

    keywords(Keyword.MENACE)

    keywordAbility(KeywordAbility.crew(4))

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "195"
        artist = "Adam Paquette"
        flavorText = "It glides around the city, a sublime combination of elegance and utility."
        imageUri = "https://cards.scryfall.io/normal/front/5/f/5fc0d1f7-c81c-4329-92b7-c4df227cc56c.jpg?1783937163"
        ruling("2017-09-29", "Each Vehicle is printed with a power and toughness, but it's not a creature. If it becomes a creature (most likely through its crew ability), it will have that power and toughness.")
        ruling("2017-09-29", "If an effect causes a Vehicle to become an artifact creature with a specified power and toughness, that effect overwrites the Vehicle's printed power and toughness.")
        ruling("2017-09-29", "Vehicle is an artifact type, not a creature type. A Vehicle that's crewed won't normally have any creature type.")
        ruling("2017-09-29", "Once a player announces that they are activating a crew ability, no player may take other actions until the ability has been paid for. Notably, players can't try to stop the ability by changing a creature's power or by removing or tapping a creature.")
        ruling("2017-09-29", "Any untapped creature you control can be tapped to pay a crew cost, even one that just came under your control.")
        ruling("2017-09-29", "You may tap more creatures than necessary to activate a crew ability.")
        ruling("2017-09-29", "Creatures that crew a Vehicle aren't attached to it or related in any other way. Effects that affect the Vehicle, such as by destroying it or giving it a +1/+1 counter, don't affect the creatures that crewed it.")
        ruling("2017-09-29", "Once a Vehicle becomes a creature, it behaves exactly like any other artifact creature. It can't attack unless you've controlled it continuously since your turn began, it can block if it's untapped, it can be tapped to pay a Vehicle's crew cost, and so on.")
    }
}
