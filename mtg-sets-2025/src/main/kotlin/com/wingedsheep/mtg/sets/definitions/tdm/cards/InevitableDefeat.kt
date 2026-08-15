package com.wingedsheep.mtg.sets.definitions.tdm.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Inevitable Defeat
 * {1}{R}{W}{B}
 * Instant
 * This spell can't be countered.
 * Exile target nonland permanent. Its controller loses 3 life and you gain 3 life.
 *
 * The life loss / gain resolves before the exile so `TargetController` can still read the
 * permanent's controller — exiling first would move it off the battlefield and the
 * controller lookup would silently fail (same ordering trick as Undermine / Agonizing
 * Demise). All three happen in one resolution, so the order is imperceptible to players.
 */
val InevitableDefeat = card("Inevitable Defeat") {
    manaCost = "{1}{R}{W}{B}"
    colorIdentity = "WBR"
    typeLine = "Instant"
    cantBeCountered = true
    oracleText = "This spell can't be countered.\n" +
        "Exile target nonland permanent. Its controller loses 3 life and you gain 3 life."

    spell {
        val permanent = target("nonland permanent", Targets.NonlandPermanent)
        effect = Effects.LoseLife(3, EffectTarget.TargetController) then
            Effects.GainLife(3, EffectTarget.Controller) then
            Effects.Exile(permanent)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "194"
        artist = "Cristi Balanescu"
        imageUri = "https://cards.scryfall.io/normal/front/9/d/9d677980-b608-407e-9f17-790a81263f15.jpg?1743204760"
    }
}
