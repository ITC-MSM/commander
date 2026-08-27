package com.wingedsheep.mtg.sets.definitions.dst.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Dragon's Claw
 * {2}
 * Artifact
 *
 * Whenever a player casts a red spell, you may gain 1 life.
 *
 * The red member of Darksteel's "Feather/Horn/Claw/Eye/Tooth" cycle — see [AngelsFeather] for the
 * shared shape. "A player" is every player, so the trigger is [Triggers.anyPlayerCasts] (binding
 * ANY, `Player.Each`) over a colour filter, and the printed "you may" is `optional = true`, which
 * lowers to a `Gate.MayDecide` around the life gain.
 */
val DragonsClaw = card("Dragon's Claw") {
    manaCost = "{2}"
    colorIdentity = ""
    typeLine = "Artifact"
    oracleText = "Whenever a player casts a red spell, you may gain 1 life."

    triggeredAbility {
        trigger = Triggers.anyPlayerCasts(GameObjectFilter.Any.withColor(Color.RED))
        optional = true
        effect = Effects.GainLife(1)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "117"
        artist = "Alan Pollack"
        flavorText = "Though no longer attached to the hand, it still holds its adversary in its grasp."
        imageUri = "https://cards.scryfall.io/normal/front/7/a/7a46bbcc-b287-47bb-b252-5dd3217f61a9.jpg?1783944425"
    }
}
