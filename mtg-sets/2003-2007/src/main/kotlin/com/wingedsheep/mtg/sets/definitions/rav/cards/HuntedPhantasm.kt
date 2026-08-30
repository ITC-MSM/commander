package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.core.AbilityFlag
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Hunted Phantasm — Ravnica: City of Guilds #55
 * {1}{U}{U} · Creature — Spirit · 4/6
 *
 * This creature can't be blocked.
 * When this creature enters, target opponent creates five 1/1 red Goblin creature tokens.
 *
 * The blue member of the Hunted cycle, and the one whose drawback is sharpest: the five Goblins can
 * never block the Phantasm itself, so they are pure offence for the defender. "Can't be blocked" is
 * the card-level [AbilityFlag.CANT_BE_BLOCKED] rather than a keyword (Tidal Kraken), and the Goblins
 * enter under the *targeted opponent's* control via [Effects.CreateToken]'s `controller`.
 */
val HuntedPhantasm = card("Hunted Phantasm") {
    manaCost = "{1}{U}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Spirit"
    oracleText = "This creature can't be blocked.\n" +
        "When this creature enters, target opponent creates five 1/1 red Goblin creature tokens."
    power = 4
    toughness = 6

    flags(AbilityFlag.CANT_BE_BLOCKED)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val opponent = target("target opponent", Targets.Opponent)
        effect = Effects.CreateToken(
            power = 1,
            toughness = 1,
            colors = setOf(Color.RED),
            creatureTypes = setOf("Goblin"),
            count = 5,
            controller = opponent,
        )
        description = "When this creature enters, target opponent creates five 1/1 red Goblin " +
            "creature tokens."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "55"
        artist = "Justin Sweet"
        imageUri = "https://cards.scryfall.io/normal/front/f/a/faec8ab3-80c6-4b8f-a60d-50cc683e66b4.jpg?1783943684"
    }
}
