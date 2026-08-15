package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.jobSelect
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantDynamicStatsEffect
import com.wingedsheep.sdk.scripting.GrantSubtype
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Machinist's Arsenal
 * {4}{W}
 * Artifact — Equipment
 * Job select (When this Equipment enters, create a 1/1 colorless Hero creature token,
 *   then attach this to it.)
 * Equipped creature gets +2/+2 for each artifact you control and is an Artificer in addition
 *   to its other types.
 * Machina — Equip {4}
 *
 * The dynamic +2/+2 per artifact is a [GrantDynamicStatsEffect] whose bonus recomputes
 * continuously: `Multiply(Count(artifacts you control), 2)`. The Equipment itself (and any
 * artifact creatures) count toward "artifacts you control", matching the printed wording.
 * "Machina" is flavor on the equip ability; it resolves as a standard equip {4}.
 */
val MachinistsArsenal = card("Machinist's Arsenal") {
    manaCost = "{4}{W}"
    colorIdentity = "W"
    typeLine = "Artifact — Equipment"
    oracleText = "Job select (When this Equipment enters, create a 1/1 colorless Hero creature token, then attach this to it.)\n" +
        "Equipped creature gets +2/+2 for each artifact you control and is an Artificer in addition to its other types.\n" +
        "Machina — Equip {4} ({4}: Attach to target creature you control. Equip only as a sorcery.)"

    jobSelect()

    staticAbility {
        ability = GrantDynamicStatsEffect(
            filter = Filters.EquippedCreature,
            powerBonus = DynamicAmount.Multiply(
                DynamicAmount.Count(
                    player = Player.You,
                    zone = Zone.BATTLEFIELD,
                    filter = GameObjectFilter.Artifact
                ),
                2
            ),
            toughnessBonus = DynamicAmount.Multiply(
                DynamicAmount.Count(
                    player = Player.You,
                    zone = Zone.BATTLEFIELD,
                    filter = GameObjectFilter.Artifact
                ),
                2
            )
        )
    }
    staticAbility {
        ability = GrantSubtype("Artificer", Filters.EquippedCreature)
    }

    equipAbility("{4}")

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "23"
        artist = "Thanh Tuấn"
        imageUri = "https://cards.scryfall.io/normal/front/f/f/ff976428-2145-4630-aab1-08870b90b2f0.jpg?1748705839"
    }
}
