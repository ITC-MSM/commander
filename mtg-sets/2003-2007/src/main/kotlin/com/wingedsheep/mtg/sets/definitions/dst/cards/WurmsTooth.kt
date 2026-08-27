package com.wingedsheep.mtg.sets.definitions.dst.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Wurm's Tooth
 * {2}
 * Artifact
 *
 * Whenever a player casts a green spell, you may gain 1 life.
 *
 * The green member of Darksteel's "Feather/Horn/Claw/Eye/Tooth" cycle — see [AngelsFeather] for the
 * shared shape. "A player" is every player, so the trigger is [Triggers.anyPlayerCasts] (binding
 * ANY, `Player.Each`) over a colour filter, and the printed "you may" is `optional = true`, which
 * lowers to a `Gate.MayDecide` around the life gain.
 */
val WurmsTooth = card("Wurm's Tooth") {
    manaCost = "{2}"
    colorIdentity = ""
    typeLine = "Artifact"
    oracleText = "Whenever a player casts a green spell, you may gain 1 life."

    triggeredAbility {
        trigger = Triggers.anyPlayerCasts(GameObjectFilter.Any.withColor(Color.GREEN))
        optional = true
        effect = Effects.GainLife(1)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "162"
        artist = "Alan Pollack"
        flavorText = "A wurm knows nothing of deception. If it opens its mouth, it plans to eat you."
        imageUri = "https://cards.scryfall.io/normal/front/4/8/482cdbe0-b865-4e09-bd30-61ab93739b53.jpg?1783944414"
    }
}
