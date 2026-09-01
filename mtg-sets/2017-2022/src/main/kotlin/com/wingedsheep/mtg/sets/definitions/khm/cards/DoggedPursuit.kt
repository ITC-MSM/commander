package com.wingedsheep.mtg.sets.definitions.khm.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Dogged Pursuit
 * {3}{B}
 * Enchantment
 * At the beginning of your end step, each opponent loses 1 life and you gain 1 life.
 *
 * A drain-on-a-stick enchantment. The two halves are separate effects rather than a single drain
 * so that the life loss reaches every opponent ([Player.EachOpponent]) while the gain stays fixed
 * at 1 for the controller — a multiplayer game drains each opponent but still gains only 1.
 */
val DoggedPursuit = card("Dogged Pursuit") {
    manaCost = "{3}{B}"
    colorIdentity = "B"
    typeLine = "Enchantment"
    oracleText = "At the beginning of your end step, each opponent loses 1 life and you gain 1 life."

    triggeredAbility {
        trigger = Triggers.YourEndStep
        effect = Effects.Composite(
            Effects.LoseLife(1, EffectTarget.PlayerRef(Player.EachOpponent)),
            Effects.GainLife(1)
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "85"
        artist = "Jason Rainville"
        flavorText = "Kaya had stalked countless horrific foes, but never one that killed with such callous precision and twisted creativity."
        imageUri = "https://cards.scryfall.io/normal/front/6/0/60f6a159-b969-4767-802e-409f8bf286fe.jpg"
    }
}
