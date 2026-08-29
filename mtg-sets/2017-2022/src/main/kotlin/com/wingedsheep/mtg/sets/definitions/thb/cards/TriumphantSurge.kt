package com.wingedsheep.mtg.sets.definitions.thb.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Triumphant Surge
 * {3}{W}
 * Instant
 *
 * Destroy target creature with power 4 or greater. You gain 3 life.
 *
 * "Power 4 or greater" is a predicate on the target filter (`powerAtLeast(4)`), which the legality
 * check evaluates against projected state — a creature pumped to 4 power this turn is a legal
 * target, and one shrunk below 4 in response stops being one. The life gain is a second sentence
 * in the same effect, so it happens whether or not the destroy still has a target.
 */
val TriumphantSurge = card("Triumphant Surge") {
    manaCost = "{3}{W}"
    colorIdentity = "W"
    typeLine = "Instant"
    oracleText = "Destroy target creature with power 4 or greater. You gain 3 life."

    spell {
        val victim = target("target", TargetCreature(filter = TargetFilter.Creature.powerAtLeast(4)))
        effect = Effects.Composite(
            Effects.Destroy(victim),
            Effects.GainLife(3),
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "41"
        artist = "Daarken"
        flavorText = "Not even death can quench a hero's inner fire."
        imageUri = "https://cards.scryfall.io/normal/front/7/5/75d6eb18-a49d-4fa5-a333-78aafbc4abcb.jpg"
    }
}
