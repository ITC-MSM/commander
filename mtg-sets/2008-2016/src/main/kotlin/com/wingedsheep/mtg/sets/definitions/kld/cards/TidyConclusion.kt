package com.wingedsheep.mtg.sets.definitions.kld.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.TargetCreature
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Tidy Conclusion
 * {3}{B}{B}
 * Instant
 *
 * Destroy target creature. You gain 1 life for each artifact you control.
 *
 * The life gain is counted on resolution, after the destroy — so an artifact creature killed by
 * this spell no longer counts itself. "Artifact you control" is a bare tribal noun, so it is every
 * artifact permanent, not just artifact creatures.
 */
val TidyConclusion = card("Tidy Conclusion") {
    manaCost = "{3}{B}{B}"
    colorIdentity = "B"
    typeLine = "Instant"
    oracleText = "Destroy target creature. You gain 1 life for each artifact you control."

    spell {
        val t = target("target", TargetCreature())
        effect = Effects.Composite(
            Effects.Destroy(t),
            Effects.GainLife(DynamicAmount.AggregateBattlefield(Player.You, GameObjectFilter.Artifact))
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "103"
        artist = "Bastien L. Deharme"
        flavorText = "Aetherborn patrons use any means necessary to secure victory for their protégés."
        imageUri = "https://cards.scryfall.io/normal/front/d/f/dfcf6849-4fac-41b9-8e70-dc77c4562a42.jpg?1783937200"
    }
}
