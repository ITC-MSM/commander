package com.wingedsheep.mtg.sets.definitions.dst.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Angel's Feather
 * {2}
 * Artifact
 *
 * Whenever a player casts a white spell, you may gain 1 life.
 *
 * One of Darksteel's five "Feather/Horn/Claw/Eye/Tooth" artifacts — a single shape with the spell
 * filter recoloured. "A player" is every player, this artifact's controller included, so the trigger
 * is [Triggers.anyPlayerCasts] (binding ANY, `Player.Each`). The filter is colour-based rather than
 * type-based: any white spell qualifies, a partly-white multicoloured spell included. The printed
 * "you may" is the builder's `optional = true`, which lowers to a `Gate.MayDecide` around the life
 * gain; the artifact's controller is always the one who decides and gains, whoever cast the spell.
 */
val AngelsFeather = card("Angel's Feather") {
    manaCost = "{2}"
    colorIdentity = ""
    typeLine = "Artifact"
    oracleText = "Whenever a player casts a white spell, you may gain 1 life."

    triggeredAbility {
        trigger = Triggers.anyPlayerCasts(GameObjectFilter.Any.withColor(Color.WHITE))
        optional = true
        effect = Effects.GainLife(1)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "92"
        artist = "Alan Pollack"
        flavorText = "If taken, it cuts the hand that clutches it. If given, it heals the hand that holds it."
        imageUri = "https://cards.scryfall.io/normal/front/4/a/4a11d101-2e82-42d5-b4a1-8f0c520441ab.jpg?1783944431"
    }
}
