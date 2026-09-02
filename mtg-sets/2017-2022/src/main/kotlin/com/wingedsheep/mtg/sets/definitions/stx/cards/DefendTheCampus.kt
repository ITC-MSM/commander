package com.wingedsheep.mtg.sets.definitions.stx.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Defend the Campus — Strixhaven: School of Mages #12 (canonical printing)
 * {3}{W} · Instant
 *
 * Choose one —
 * • Creatures you control get +1/+1 until end of turn.
 * • Destroy target creature with power 4 or greater.
 *
 * An ordinary "choose one" `modal`. The first mode is the untargeted team pump,
 * [Patterns.Group.modifyStatsForAll] over the creatures you control; the second is [Effects.Destroy]
 * whose target is narrowed to `powerAtLeast(4)`, which the legality check reads off projected state
 * so a creature pumped to 4 this turn is a legal target.
 */
val DefendTheCampus = card("Defend the Campus") {
    manaCost = "{3}{W}"
    colorIdentity = "W"
    typeLine = "Instant"
    oracleText =
        "Choose one —\n" +
        "• Creatures you control get +1/+1 until end of turn.\n" +
        "• Destroy target creature with power 4 or greater."

    spell {
        modal {
            mode(
                "Creatures you control get +1/+1 until end of turn",
                Patterns.Group.modifyStatsForAll(1, 1, GroupFilter.AllCreaturesYouControl)
            )
            mode("Destroy target creature with power 4 or greater") {
                val victim = target(
                    "target",
                    TargetCreature(filter = TargetFilter(GameObjectFilter.Creature.powerAtLeast(4)))
                )
                effect = Effects.Destroy(victim)
            }
        }
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "12"
        artist = "Izzy"
        flavorText = "Professors scrambled to push the mage hunters back, leaving the heart of Strixhaven undefended."
        imageUri = "https://cards.scryfall.io/normal/front/8/5/85e4e1b5-77d6-4af4-b22e-6f6b4d129f5d.jpg?1783927394"
    }
}
