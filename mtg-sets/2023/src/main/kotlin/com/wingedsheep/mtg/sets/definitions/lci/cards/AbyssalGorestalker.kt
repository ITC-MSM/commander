package com.wingedsheep.mtg.sets.definitions.lci.cards

import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.ForEachPlayerEffect
import com.wingedsheep.sdk.scripting.effects.ForceSacrificeEffect
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Abyssal Gorestalker
 * {4}{B}{B}
 * Creature — Horror
 * 6/6
 *
 * When this creature enters, each player sacrifices two creatures of their choice.
 *
 * Implemented as an ETB triggered ability using [ForEachPlayerEffect] over [Player.Each].
 * Inside the loop, [EffectTarget.Controller] is rebound to the currently iterated player
 * (CR 701.21), so each player independently chooses which two of their own creatures to
 * sacrifice. When a player controls fewer than two creatures the [ForceSacrificeExecutor]
 * auto-sacrifices all valid candidates without prompting (sacrifices as many as possible).
 */
val AbyssalGorestalker = card("Abyssal Gorestalker") {
    manaCost = "{4}{B}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Horror"
    power = 6
    toughness = 6
    oracleText = "When this creature enters, each player sacrifices two creatures of their choice."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = ForEachPlayerEffect(
            players = Player.Each,
            effects = listOf(
                ForceSacrificeEffect(
                    filter = GameObjectFilter.Creature,
                    count = 2,
                    target = EffectTarget.Controller
                )
            )
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "87"
        artist = "Maxime Minard"
        imageUri = "https://cards.scryfall.io/normal/front/a/5/a559f77f-1f10-475b-9361-7f297d50f254.jpg?1782694541"
    }
}
