package com.wingedsheep.mtg.sets.definitions.leg.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Shield Wall
 * {1}{W}
 * Instant
 *
 * Creatures you control get +0/+2 until end of turn.
 */
val ShieldWall = card("Shield Wall") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Instant"
    oracleText = "Creatures you control get +0/+2 until end of turn."

    spell {
        effect = Effects.ForEachInGroup(
            GroupFilter(GameObjectFilter.Creature.youControl()),
            Effects.ModifyStats(0, 2, EffectTarget.Self),
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "36"
        artist = "Douglas Shuler"
        imageUri = "https://cards.scryfall.io/normal/front/a/5/a5032bf0-f9c0-4ef0-8ec2-fe7ccea9bdf3.jpg?1783948080"
    }
}
