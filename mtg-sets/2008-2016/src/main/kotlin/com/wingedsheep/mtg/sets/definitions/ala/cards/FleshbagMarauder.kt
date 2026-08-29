package com.wingedsheep.mtg.sets.definitions.ala.cards

import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.ForceSacrificeEffect
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Fleshbag Marauder
 * {2}{B}
 * Creature — Zombie Warrior
 * 3/1
 *
 * When this creature enters, each player sacrifices a creature of their choice.
 *
 * "each player" includes you, and the Marauder itself is a legal choice for its own controller —
 * `Player.Each` with no `excludeSelf`, matching the printed symmetry.
 */
val FleshbagMarauder = card("Fleshbag Marauder") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Zombie Warrior"
    oracleText = "When this creature enters, each player sacrifices a creature of their choice."
    power = 3
    toughness = 1

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = ForceSacrificeEffect(
            GameObjectFilter.Creature,
            1,
            EffectTarget.PlayerRef(Player.Each)
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "76"
        artist = "Pete Venters"
        flavorText = "Grixis is a world where the only things found in abundance are death and decay. Corpses, whole or in part, are the standard currency among necromancers and demons."
        imageUri = "https://cards.scryfall.io/normal/front/f/7/f71e4391-04a8-4df8-9d52-3a3480bcd5b6.jpg"
    }
}
