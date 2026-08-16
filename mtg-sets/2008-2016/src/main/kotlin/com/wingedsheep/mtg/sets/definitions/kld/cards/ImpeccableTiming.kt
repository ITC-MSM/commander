package com.wingedsheep.mtg.sets.definitions.kld.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetPermanent

/**
 * Impeccable Timing
 * {1}{W}
 * Instant
 * Impeccable Timing deals 3 damage to target attacking or blocking creature.
 */
val ImpeccableTiming = card("Impeccable Timing") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Instant"
    oracleText = "Impeccable Timing deals 3 damage to target attacking or blocking creature."

    spell {
        val t = target("target", TargetPermanent(filter = TargetFilter.AttackingOrBlockingCreature))
        effect = Effects.DealDamage(3, t)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "19"
        artist = "Chris Rallis"
        flavorText = "When Baral constructed his trap for Chandra, he did not account for the arrival of an enormous leonin wielding a twin-headed axe."
        imageUri = "https://cards.scryfall.io/normal/front/2/9/2921c95e-bf2f-409b-a41d-86d873690562.jpg?1783937231"
    }
}
