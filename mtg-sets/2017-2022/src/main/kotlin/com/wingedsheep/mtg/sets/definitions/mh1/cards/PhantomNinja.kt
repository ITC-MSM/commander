package com.wingedsheep.mtg.sets.definitions.mh1.cards

import com.wingedsheep.sdk.core.AbilityFlag
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Phantom Ninja — Modern Horizons #62
 * {1}{U}{U} · Creature — Illusion Ninja · 2 / 2
 *
 * This creature can't be blocked.
 *
 * Unconditional evasion, so it is the [AbilityFlag.CANT_BE_BLOCKED] static flag rather than a
 * granted keyword.
 */
val PhantomNinja = card("Phantom Ninja") {
    manaCost = "{1}{U}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Illusion Ninja"
    power = 2
    toughness = 2
    oracleText = "This creature can't be blocked."

    flags(AbilityFlag.CANT_BE_BLOCKED)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "62"
        artist = "Joe Slucher"
        flavorText = "\"Ninjas can run across water, pull ladders from pockets, kill with a kiss, and slip between bricks. Pack of lies, I say.\"\n—Benden, teahouse gossip"
        imageUri = "https://cards.scryfall.io/normal/front/a/3/a392b557-e809-4371-92d7-6e93caed4f1b.jpg?1783933140"
    }
}
