package com.wingedsheep.mtg.sets.definitions.ala.cards

import com.wingedsheep.sdk.core.AbilityFlag
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Jhessian Infiltrator
 * {G}{U}
 * Creature — Human Rogue
 * 2 / 2
 * This creature can't be blocked.
 *
 * Unconditional evasion on a creature is the card-level [AbilityFlag.CANT_BE_BLOCKED] flag, which the
 * block-legality rules read straight off the projected permanent. (The
 * [com.wingedsheep.sdk.scripting.CantBeBlocked] static ability is only needed when the restriction is
 * conditional, or when it lives on an attachment and must name `GroupFilter.attachedCreature()`.)
 */
val JhessianInfiltrator = card("Jhessian Infiltrator") {
    manaCost = "{G}{U}"
    colorIdentity = "GU"
    typeLine = "Creature — Human Rogue"
    power = 2
    toughness = 2
    oracleText = "This creature can't be blocked."

    flags(AbilityFlag.CANT_BE_BLOCKED)

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "174"
        artist = "Donato Giancola"
        flavorText = "The Jhessian navy makes successful raids on Valeron's coastal towns thanks to their spies planted during peacetime."
        imageUri = "https://cards.scryfall.io/normal/front/1/7/1761d867-2eb0-406b-b175-97a90c457844.jpg"
    }
}
