package com.wingedsheep.mtg.sets.definitions.p02.cards

import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CantAttackUnless

/**
 * Steam Frigate
 * {2}{U}
 * Creature — Human Pirate
 * 3/3
 *
 * This creature can't attack unless defending player controls an Island.
 */
val SteamFrigate = card("Steam Frigate") {
    manaCost = "{2}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Human Pirate"
    oracleText = "This creature can't attack unless defending player controls an Island."
    power = 3
    toughness = 3

    staticAbility {
        ability = CantAttackUnless(Conditions.DefendingPlayerControlsLandType("Island"))
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "47"
        artist = "Mark Tedin"
        flavorText = "\"Is it merchants or is it pirates?\"\n\"It's Talas—there's no difference.\""
        imageUri = "https://cards.scryfall.io/normal/front/e/c/ec3193cb-2c00-4ef8-bf2e-d3b1e3b875a3.jpg"
    }
}
