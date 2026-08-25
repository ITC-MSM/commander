package com.wingedsheep.mtg.sets.definitions.mh1.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.ninjutsu
import com.wingedsheep.sdk.model.Rarity

/**
 * Ninja of the New Moon
 * {3}{B}{B}
 * Creature — Spirit Ninja
 * 6/3
 * Ninjutsu {3}{B} ({3}{B}, Return an unblocked attacker you control to hand: Put this card onto the battlefield from your hand tapped and attacking.)
 */
val NinjaOfTheNewMoon = card("Ninja of the New Moon") {
    manaCost = "{3}{B}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Spirit Ninja"
    power = 6
    toughness = 3
    oracleText = "Ninjutsu {3}{B} ({3}{B}, Return an unblocked attacker you control to hand: Put this card onto the battlefield from your hand tapped and attacking.)"

    ninjutsu("{3}{B}")

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "99"
        artist = "Greg Opalinski"
        flavorText = "The night is the greatest ally of all."
        imageUri = "https://cards.scryfall.io/normal/front/0/8/08be60eb-15ec-4112-919c-995062a9ed54.jpg?1783933124"
    }
}
