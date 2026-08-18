package com.wingedsheep.mtg.sets.definitions.mrd.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.Duration
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Grab the Reins
 * {3}{R}
 * Instant
 * Choose one —
 * • Until end of turn, you gain control of target creature and it gains haste.
 * • Sacrifice a creature. Grab the Reins deals damage equal to that creature's power to any target.
 * Entwine {2}{R} (Choose both if you pay the entwine cost.)
 */
val GrabTheReins = card("Grab the Reins") {
    manaCost = "{3}{R}"
    colorIdentity = "R"
    typeLine = "Instant"
    oracleText = "Choose one —\n" +
        "• Until end of turn, you gain control of target creature and it gains haste.\n" +
        "• Sacrifice a creature. Grab the Reins deals damage equal to that creature's power to any target.\n" +
        "Entwine {2}{R} (Choose both if you pay the entwine cost.)"

    spell {
        modal(
            chooseCount = 2,
            minChooseCount = 1,
            additionalManaCostPerExtraMode = "{2}{R}",
        ) {
            mode("Gain control of target creature until end of turn; it gains haste") {
                val creature = target("creature to gain control of", Targets.Creature)
                effect = Effects.Composite(
                    Effects.GainControl(creature, Duration.EndOfTurn),
                    Effects.GrantKeyword(Keyword.HASTE, creature),
                )
            }
            mode("Sacrifice a creature; deal damage equal to its power to any target") {
                val damageTarget = target("damage target", Targets.Any)
                effect = Effects.SacrificeOwn(GameObjectFilter.Creature)
                    .then(Effects.DealDamage(DynamicAmounts.sacrificedPower(), damageTarget))
            }
        }
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "95"
        artist = "Michael Sutfin"
        imageUri = "https://cards.scryfall.io/normal/front/5/7/570fa211-e937-4ad7-bc72-60f8adc3203d.jpg?1783944540"
        ruling(
            "2004-12-01",
            "If you pay the entwine cost, you can sacrifice the creature you gain control of with Grab the Reins.",
        )
        ruling(
            "2004-12-01",
            "If you choose the sacrifice mode, choose the damage target as you cast the spell, but choose " +
                "the creature to sacrifice as it resolves. You must sacrifice a creature if possible; if you " +
                "control none, no damage is dealt.",
        )
        ruling(
            "2004-12-01",
            "An entwined Grab the Reins resolves as one spell; no player gets priority between gaining " +
                "control of the creature and sacrificing a creature.",
        )
    }
}
