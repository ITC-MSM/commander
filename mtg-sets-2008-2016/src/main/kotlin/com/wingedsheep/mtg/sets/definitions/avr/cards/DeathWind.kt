package com.wingedsheep.mtg.sets.definitions.avr.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Death Wind
 * {X}{B}
 * Instant
 * Target creature gets -X/-X until end of turn.
 *
 * Canonical definition lives in Avacyn Restored (earliest real printing).
 * Reprinted in Dragons of Tarkir — see DTK `DeathWindReprint`.
 */
val DeathWind = card("Death Wind") {
    manaCost = "{X}{B}"
    colorIdentity = "B"
    typeLine = "Instant"
    oracleText = "Target creature gets -X/-X until end of turn."

    spell {
        val creature = target("creature", Targets.Creature)
        val negX = DynamicAmount.Multiply(DynamicAmount.XValue, -1)
        effect = Effects.ModifyStats(negX, negX, creature)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "93"
        artist = "Tomasz Jedruszek"
        flavorText = "Leukin stared at the smoldering angel feathers. \"Run!\" he screamed to his patrol. \"We don't stand a chance!\""
        imageUri = "https://cards.scryfall.io/normal/front/4/6/462a0961-cca5-4d63-867f-7426dbef8639.jpg?1592708809"
    }
}
