package com.wingedsheep.mtg.sets.definitions.avr.cards

import com.wingedsheep.sdk.core.AbilityFlag
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Latch Seeker
 * {1}{U}{U}
 * Creature — Spirit
 * 3 / 1
 *
 * This creature can't be blocked.
 *
 * Unconditional unblockability is an [AbilityFlag] on the card itself (Triton Shorestalker shape),
 * not a static ability.
 */
val LatchSeeker = card("Latch Seeker") {
    manaCost = "{1}{U}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Spirit"
    power = 3
    toughness = 1
    oracleText = "This creature can't be blocked."

    flags(AbilityFlag.CANT_BE_BLOCKED)

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "63"
        artist = "Vincent Proce"
        flavorText = "It seeks a tomb that will hold it, a coffin that will give it rest."
        imageUri = "https://cards.scryfall.io/normal/front/3/e/3e4e7589-9cee-4d57-8648-ce733781bfb2.jpg?1783940717"
    }
}
