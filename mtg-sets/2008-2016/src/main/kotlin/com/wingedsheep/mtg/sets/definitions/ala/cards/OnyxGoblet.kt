package com.wingedsheep.mtg.sets.definitions.ala.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.TargetPlayer

/**
 * Onyx Goblet
 * {2}{B}
 * Artifact
 * {T}: Target player loses 1 life.
 *
 * A coloured artifact whose only ability is a bare [Costs.Tap] activation. The target is a plain
 * [TargetPlayer] (any player, not just an opponent) and the drain is [Effects.LoseLife] with a
 * fixed amount pointed at that bound variable — no life gain rider, so nothing composes here.
 */
val OnyxGoblet = card("Onyx Goblet") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Artifact"
    oracleText = "{T}: Target player loses 1 life."

    activatedAbility {
        cost = Costs.Tap
        val t = target("target", TargetPlayer())
        effect = Effects.LoseLife(1, t)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "81"
        artist = "rk post"
        flavorText = "The goblet was a gift from the sphinx Gorael, who hoped humans and vedalken would eventually destroy each other to acquire it, leaving all of Esper to her own kind."
        imageUri = "https://cards.scryfall.io/normal/front/3/6/3686eb57-e9c8-480f-8ab4-2894e1b5fe20.jpg"
    }
}
