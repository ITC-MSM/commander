package com.wingedsheep.mtg.sets.definitions.ala.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Branching Bolt
 * {1}{R}{G}
 * Instant
 * Choose one or both —
 * • Branching Bolt deals 3 damage to target creature with flying.
 * • Branching Bolt deals 3 damage to target creature without flying.
 *
 * "Choose one or both" is a *count*, not a third mode: `modal(chooseCount = 2, minChooseCount = 1)`
 * (CR 700.2). Each mode carries its own target, so picking both asks for two creatures and each
 * mode's legality is checked independently. The flying split is a predicate pair on the target
 * filters — `withKeyword(FLYING)` and `withoutKeyword(FLYING)` — which reads projected state, so a
 * creature that gains or loses flying in response changes which mode can still see it.
 */
val BranchingBolt = card("Branching Bolt") {
    manaCost = "{1}{R}{G}"
    colorIdentity = "GR"
    typeLine = "Instant"
    oracleText = "Choose one or both —\n" +
        "• Branching Bolt deals 3 damage to target creature with flying.\n" +
        "• Branching Bolt deals 3 damage to target creature without flying."

    spell {
        modal(chooseCount = 2, minChooseCount = 1) {
            mode("Branching Bolt deals 3 damage to target creature with flying") {
                val flier = target(
                    "target",
                    TargetCreature(filter = TargetFilter.Creature.withKeyword(Keyword.FLYING))
                )
                effect = Effects.DealDamage(3, flier)
            }
            mode("Branching Bolt deals 3 damage to target creature without flying") {
                val grounded = target(
                    "target",
                    TargetCreature(filter = TargetFilter.Creature.withoutKeyword(Keyword.FLYING))
                )
                effect = Effects.DealDamage(3, grounded)
            }
        }
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "158"
        artist = "Vance Kovacs"
        flavorText = "\"Lightning lives in everything, in living flesh and growing things. It must be set free.\"\n—Rakka Mar"
        imageUri = "https://cards.scryfall.io/normal/front/e/7/e7468876-f401-4a75-81c0-bed09cdda3e1.jpg"
    }
}
