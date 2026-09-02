package com.wingedsheep.mtg.sets.definitions.rtr.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Daggerdrome Imp
 * {1}{B}
 * Creature — Imp
 * 1/1
 *
 * Flying
 * Lifelink (Damage dealt by this creature also causes you to gain that much life.)
 *
 * Canonical printing: Return to Ravnica, the card's earliest real printing.
 *
 * Two evergreen keywords and nothing else.
 */
val DaggerdromeImp = card("Daggerdrome Imp") {
    manaCost = "{1}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Imp"
    oracleText = "Flying\n" +
        "Lifelink (Damage dealt by this creature also causes you to gain that much life.)"
    power = 1
    toughness = 1

    keywords(Keyword.FLYING, Keyword.LIFELINK)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "60"
        artist = "Jack Wang"
        flavorText = "One of the many reasons why open-air markets close at dusk."
        imageUri = "https://cards.scryfall.io/normal/front/7/0/70639887-bdba-4879-a3f8-c716f97fc325.jpg?1783940364"
    }
}
