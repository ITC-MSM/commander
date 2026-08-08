package com.wingedsheep.mtg.sets.definitions.msh.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.teamwork
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Go Nuts! — Marvel Super Heroes #168
 * {G} · Sorcery · Common
 *
 * Teamwork 3 (As an additional cost to cast this spell, you may tap any number of creatures you
 * control with total power 3 or more.)
 * Choose one. If this spell was cast using teamwork, choose both instead.
 * • Put a +1/+1 counter on target creature.
 * • Target creature you control fights target creature an opponent controls.
 *
 * The modal shape of teamwork (CR 702.194c): the cast-time [DynamicAmount.Conditional] on
 * [Conditions.TeamworkWasPaid] narrows the printed `chooseCount = 2` to 1 unless the teamwork cost
 * was declared. Modes resolve in printed order, so a teamwork cast that puts the counter on the
 * same creature that then fights sends the bigger body into the fight (CR 608.2c — the modes are one
 * resolution, and its controller follows the instructions in the order written).
 */
val GoNuts = card("Go Nuts!") {
    manaCost = "{G}"
    colorIdentity = "G"
    typeLine = "Sorcery"
    oracleText = "Teamwork 3 (As an additional cost to cast this spell, you may tap any number of " +
        "creatures you control with total power 3 or more.)\n" +
        "Choose one. If this spell was cast using teamwork, choose both instead.\n" +
        "• Put a +1/+1 counter on target creature.\n" +
        "• Target creature you control fights target creature an opponent controls."

    teamwork(3)

    spell {
        modal(
            chooseCount = 2,
            minChooseCount = 1,
            dynamicChooseCount = DynamicAmount.Conditional(
                condition = Conditions.TeamworkWasPaid,
                ifTrue = DynamicAmount.Fixed(2),
                ifFalse = DynamicAmount.Fixed(1),
            ),
        ) {
            mode("Put a +1/+1 counter on target creature") {
                val creature = target("target creature", Targets.Creature)
                effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, creature)
            }
            mode("Target creature you control fights target creature an opponent controls") {
                val yours = target("target creature you control", Targets.CreatureYouControl)
                val theirs = target(
                    "target creature an opponent controls",
                    Targets.CreatureOpponentControls,
                )
                effect = Effects.Fight(yours, theirs)
            }
        }
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "168"
        artist = "Ignatius Budi"
        imageUri = "https://cards.scryfall.io/normal/front/1/5/152a7b5b-2d95-45d3-8fd9-0ca1d5a79f8b.jpg?1783902918"
    }
}
