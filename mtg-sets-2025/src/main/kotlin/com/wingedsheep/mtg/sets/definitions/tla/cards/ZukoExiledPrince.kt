package com.wingedsheep.mtg.sets.definitions.tla.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.firebending
import com.wingedsheep.sdk.model.Rarity

/**
 * Zuko, Exiled Prince
 * {3}{R}
 * Legendary Creature — Human Noble
 * 4/3
 *
 * Firebending 3 (Whenever this creature attacks, add {R}{R}{R}. This mana lasts until end of combat.)
 * {3}: Exile the top card of your library. You may play that card this turn.
 *
 * Firebending uses the keyword facade [firebending]. The activated ability is a plain impulse draw
 * ([Patterns.Exile.impulse]) for {3}: exile the top card of the library and grant permission to play
 * it until end of turn.
 */
val ZukoExiledPrince = card("Zuko, Exiled Prince") {
    manaCost = "{3}{R}"
    colorIdentity = "R"
    typeLine = "Legendary Creature — Human Noble"
    power = 4
    toughness = 3
    oracleText = "Firebending 3 (Whenever this creature attacks, add {R}{R}{R}. This mana lasts until end of combat.)\n" +
        "{3}: Exile the top card of your library. You may play that card this turn."

    firebending(3)

    activatedAbility {
        cost = Costs.Mana("{3}")
        effect = Patterns.Exile.impulse(1)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "163"
        artist = "Nijihayashi"
        flavorText = "\"I want the Avatar. I want my honor, my throne.\""
        imageUri = "https://cards.scryfall.io/normal/front/6/a/6a73b372-9c0e-4a85-89d2-440163330687.jpg?1764121115"
    }
}
