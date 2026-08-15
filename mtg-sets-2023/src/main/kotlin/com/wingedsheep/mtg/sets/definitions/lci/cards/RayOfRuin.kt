package com.wingedsheep.mtg.sets.definitions.lci.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetPermanent

/**
 * Ray of Ruin
 * {4}{B}
 * Sorcery
 *
 * Exile target creature, Vehicle, or nonbasic land. Scry 1.
 *
 * The target requirement composes three atomic [GameObjectFilter]s with `or`:
 * creature, a permanent with the Vehicle artifact subtype, and any nonbasic
 * land. The resolution is a [Effects.Composite] of the atomic [Effects.Exile]
 * on the chosen target followed by [Effects.Scry] 1.
 */
val RayOfRuin = card("Ray of Ruin") {
    manaCost = "{4}{B}"
    colorIdentity = "B"
    typeLine = "Sorcery"
    oracleText = "Exile target creature, Vehicle, or nonbasic land. Scry 1."

    spell {
        val t = target(
            "target",
            TargetPermanent(
                filter = TargetFilter(
                    GameObjectFilter.Creature or
                        GameObjectFilter.Permanent.withSubtype("Vehicle") or
                        GameObjectFilter.NonbasicLand
                )
            )
        )
        effect = Effects.Composite(
            Effects.Exile(t),
            Effects.Scry(1)
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "117"
        artist = "Sam Rowan"
        flavorText = "\"We know Chimil's light as a warming, healing presence, but make no mistake: to those who would desecrate her lands, it is a terrible force of scouring fury.\"\n—Akal Pakal, First Steward of Oteclan"
        imageUri = "https://cards.scryfall.io/normal/front/d/4/d440d90e-ac7e-4715-971a-700c977c7fde.jpg?1782694518"
    }
}
