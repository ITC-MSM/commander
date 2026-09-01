package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.ForEachPlayerEffect
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.TargetPermanent
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Incendiary Command
 * {3}{R}{R}
 * Sorcery
 * Choose two —
 * • Incendiary Command deals 4 damage to target player or planeswalker.
 * • Incendiary Command deals 2 damage to each creature.
 * • Destroy target nonbasic land.
 * • Each player discards all the cards in their hand, then draws that many cards.
 *
 * The red member of the Lorwyn Command cycle. The wheel mode is the only one worth a note: it is
 * **per player**, not a shared count, so it runs inside a [ForEachPlayerEffect] over
 * [Player.Each]. That rebinds `controllerId` per iteration and — crucially — gives each iteration
 * fresh stored collections, so the `discardedHand_count` that
 * [Patterns.Hand.discardHand] publishes is that player's own hand size. Hoisting the discard out
 * of the loop would draw everyone the same number of cards.
 */
val IncendiaryCommand = card("Incendiary Command") {
    manaCost = "{3}{R}{R}"
    colorIdentity = "R"
    typeLine = "Sorcery"
    oracleText = "Choose two —\n" +
        "• Incendiary Command deals 4 damage to target player or planeswalker.\n" +
        "• Incendiary Command deals 2 damage to each creature.\n" +
        "• Destroy target nonbasic land.\n" +
        "• Each player discards all the cards in their hand, then draws that many cards."

    spell {
        modal(chooseCount = 2) {
            mode("Incendiary Command deals 4 damage to target player or planeswalker") {
                val victim = target("damage player or planeswalker", Targets.PlayerOrPlaneswalker)
                effect = Effects.DealDamage(4, victim)
            }
            mode("Incendiary Command deals 2 damage to each creature") {
                effect = Patterns.Group.dealDamageToAll(2, GroupFilter.AllCreatures)
            }
            mode("Destroy target nonbasic land") {
                val land = target(
                    "nonbasic land to destroy",
                    TargetPermanent(filter = TargetFilter.NonbasicLand)
                )
                effect = Effects.Destroy(land)
            }
            mode("Each player discards all the cards in their hand, then draws that many cards") {
                effect = ForEachPlayerEffect(
                    players = Player.Each,
                    effects = listOf(
                        Patterns.Hand.discardHand(),
                        Effects.DrawCards(DynamicAmount.VariableReference("discardedHand_count"))
                    )
                )
            }
        }
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "179"
        artist = "Wayne England"
        imageUri = "https://cards.scryfall.io/normal/front/5/1/512367a2-f8f6-4c28-9eb3-8e04d2694e4b.jpg?1783942873"
    }
}
