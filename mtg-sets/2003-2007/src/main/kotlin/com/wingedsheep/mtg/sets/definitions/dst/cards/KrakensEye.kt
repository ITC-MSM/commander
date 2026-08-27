package com.wingedsheep.mtg.sets.definitions.dst.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Kraken's Eye
 * {2}
 * Artifact
 *
 * Whenever a player casts a blue spell, you may gain 1 life.
 *
 * The blue member of Darksteel's "Feather/Horn/Claw/Eye/Tooth" cycle — see [AngelsFeather] for the
 * shared shape. "A player" is every player, so the trigger is [Triggers.anyPlayerCasts] (binding
 * ANY, `Player.Each`) over a colour filter, and the printed "you may" is `optional = true`, which
 * lowers to a `Gate.MayDecide` around the life gain.
 */
val KrakensEye = card("Kraken's Eye") {
    manaCost = "{2}"
    colorIdentity = ""
    typeLine = "Artifact"
    oracleText = "Whenever a player casts a blue spell, you may gain 1 life."

    triggeredAbility {
        trigger = Triggers.anyPlayerCasts(GameObjectFilter.Any.withColor(Color.BLUE))
        optional = true
        effect = Effects.GainLife(1)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "126"
        artist = "Alan Pollack"
        flavorText = "Bright as a mirror, dark as the sea."
        imageUri = "https://cards.scryfall.io/normal/front/c/c/cc767637-627a-4ea2-873b-d8a80ccc925b.jpg?1783944423"
    }
}
