package com.wingedsheep.mtg.sets.definitions.otj.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetCreature
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Gold Rush
 * {1}{G}
 * Instant
 * Create a Treasure token. Until end of turn, up to one target creature gets +2/+2 for each
 * Treasure you control.
 *
 * Ruling: count Treasures you control as Gold Rush resolves; the bonus is locked in then and
 * won't change if your Treasure count does later this turn. The Treasure created by this spell
 * itself is counted, so the buff is +2/+2 times (existing Treasures + 1).
 */
val GoldRush = card("Gold Rush") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Instant"
    oracleText = "Create a Treasure token. Until end of turn, up to one target creature gets +2/+2 for each Treasure you control."

    spell {
        target = TargetCreature(optional = true)
        // Per-Treasure buff: 2 x (number of Treasures you control). Evaluated and locked at
        // resolution by ModifyStatsExecutor. The Treasure created above is on the battlefield
        // before this runs, so it is included in the count.
        val perTreasure = DynamicAmount.Multiply(
            DynamicAmounts.battlefield(
                Player.You,
                GameObjectFilter.Artifact.withSubtype(Subtype.TREASURE)
            ).count(),
            2
        )
        effect = Effects.Composite(
            Effects.CreateTreasure(1),
            Effects.ModifyStats(perTreasure, perTreasure, EffectTarget.ContextTarget(0))
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "166"
        artist = "Eric Wilkerson"
        flavorText = "\"Don't believe anyone who tells you to bite it. If it's the real thing, you'll know.\""
        imageUri = "https://cards.scryfall.io/normal/front/1/5/15845be1-919d-4450-9de6-3552c52e8623.jpg?1712355933"

        ruling("2024-04-12", "You don't have to choose a target for Gold Rush. However, if you do, and that creature is an illegal target at the time Gold Rush tries to resolve, it won't resolve and none of its effects will happen. You won't create a Treasure token.")
        ruling("2024-04-12", "Count the number of Treasures you control as Gold Rush resolves to determine how big a bonus the target creature receives. That bonus won't change even if the number of Treasures you control does during the turn.")
    }
}
