package com.wingedsheep.mtg.sets.definitions.drk.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Morale
 * {1}{W}{W}
 * Instant
 * Attacking creatures get +1/+1 until end of turn.
 *
 * Every attacking creature, not just yours — the group is snapshotted at resolution, so a
 * creature that starts attacking afterwards is not pumped.
 */
val Morale = card("Morale") {
    manaCost = "{1}{W}{W}"
    colorIdentity = "W"
    typeLine = "Instant"
    oracleText = "Attacking creatures get +1/+1 until end of turn."

    spell {
        effect = Effects.ForEachInGroup(
            GroupFilter.AttackingCreatures,
            Effects.ModifyStats(1, 1, EffectTarget.Self)
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "14"
        artist = "Mark Poole"
        flavorText = "\"After Lacjsi's speech, the Knights grew determined to crush their ancient enemies clan by clan.\" —Tivadar of Thorn, *History of the Goblin Wars*"
        imageUri = "https://cards.scryfall.io/normal/front/c/4/c4104546-abd9-4bfb-a65e-5928cdd4522f.jpg?1783947946"
    }
}
