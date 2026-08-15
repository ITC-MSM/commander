package com.wingedsheep.mtg.sets.definitions.ktk.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature
import com.wingedsheep.sdk.scripting.targets.TargetSpell

/**
 * Temur Charm
 * {G}{U}{R}
 * Instant
 * Choose one —
 * • Target creature you control gets +1/+1 until end of turn. It fights target creature you don't control.
 * • Counter target spell unless its controller pays {3}.
 * • Creatures with power 3 or less can't block this turn.
 */
val TemurCharm = card("Temur Charm") {
    manaCost = "{G}{U}{R}"
    colorIdentity = "URG"
    typeLine = "Instant"
    oracleText = "Choose one —\n• Target creature you control gets +1/+1 until end of turn. It fights target creature you don't control.\n• Counter target spell unless its controller pays {3}.\n• Creatures with power 3 or less can't block this turn."

    spell {
        modal(chooseCount = 1) {
            mode("Target creature you control gets +1/+1 until end of turn. It fights target creature you don't control") {
                val yourCreature = target("creature you control", TargetCreature(
                    filter = TargetFilter(GameObjectFilter.Creature.youControl())
                ))
                val theirCreature = target("creature you don't control", TargetCreature(
                    filter = TargetFilter(GameObjectFilter.Creature.opponentControls())
                ))
                effect = Effects.ModifyStats(1, 1, yourCreature)
                    .then(Effects.Fight(yourCreature, theirCreature))
            }
            mode("Counter target spell unless its controller pays {3}") {
                target("target", TargetSpell())
                effect = Effects.CounterUnlessPays("{3}")
            }
            mode("Creatures with power 3 or less can't block this turn") {
                effect = Effects.CantBlockGroup(GroupFilter.AllCreatures.powerAtMost(3))
            }
        }
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "208"
        artist = "Mathias Kollros"
        imageUri = "https://cards.scryfall.io/normal/front/e/2/e2ee3e36-a849-42b0-b84b-027a08427c35.jpg?1562794960"
        ruling("2014-09-20", "You can't cast Temur Charm choosing the first mode unless you target both a creature you control and a creature you don't control.")
        ruling("2014-09-20", "If the first mode was chosen and either target is an illegal target when Temur Charm tries to resolve, neither creature will deal or be dealt damage.")
        ruling("2014-09-20", "If the first mode was chosen and just the creature you control is an illegal target when Temur Charm tries to resolve, it won't get +1/+1 until end of turn. If the creature you control is a legal target but the creature you don't control isn't, the creature you control will get +1/+1 until end of turn.")
        ruling("2014-09-20", "If the third mode was chosen, creatures with power 3 or less that enter the battlefield after Temur Charm resolves also can't block. A creature whose power was 4 or greater when Temur Charm resolved but is 3 or less as the declare blockers step begins also can't block.")
    }
}
