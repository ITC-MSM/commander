package com.wingedsheep.mtg.sets.definitions.rtr.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Auger Spree
 * {1}{B}{R}
 * Instant
 *
 * Target creature gets +4/-4 until end of turn.
 */
val AugerSpree = card("Auger Spree") {
    manaCost = "{1}{B}{R}"
    colorIdentity = "BR"
    typeLine = "Instant"
    oracleText = "Target creature gets +4/-4 until end of turn."

    spell {
        target = Targets.Creature
        effect = Effects.ModifyStats(4, -4, EffectTarget.ContextTarget(0))
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "144"
        artist = "Raymond Swanland"
        flavorText = "\"Finally, a weapon the Boros can't confiscate!\"\n—Juri, proprietor of the Juri Revue"
        imageUri = "https://cards.scryfall.io/normal/front/9/5/9580a40b-b413-4f0d-9b38-13903a9d367d.jpg?1783940345"
    }
}
