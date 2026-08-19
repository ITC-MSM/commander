package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Consuming Bonfire
 * {3}{R}{R}
 * Kindred Sorcery — Elemental
 * Choose one —
 * • Consuming Bonfire deals 4 damage to target non-Elemental creature.
 * • Consuming Bonfire deals 7 damage to target Treefolk creature.
 *
 * Each mode carries its own target, so the mode is chosen first and only that mode's target is
 * declared — an Elemental is never a legal target for the first mode.
 */
val ConsumingBonfire = card("Consuming Bonfire") {
    manaCost = "{3}{R}{R}"
    colorIdentity = "R"
    typeLine = "Kindred Sorcery — Elemental"
    oracleText = "Choose one —\n" +
        "• Consuming Bonfire deals 4 damage to target non-Elemental creature.\n" +
        "• Consuming Bonfire deals 7 damage to target Treefolk creature."

    spell {
        modal(chooseCount = 1) {
            mode("Consuming Bonfire deals 4 damage to target non-Elemental creature") {
                val creature = target(
                    "target non-Elemental creature",
                    TargetCreature(filter = TargetFilter(GameObjectFilter.Creature.notSubtype(Subtype.ELEMENTAL)))
                )
                effect = Effects.DealDamage(4, creature)
            }
            mode("Consuming Bonfire deals 7 damage to target Treefolk creature") {
                val treefolk = target(
                    "target Treefolk creature",
                    TargetCreature(filter = TargetFilter.Creature.withSubtype(Subtype.TREEFOLK))
                )
                effect = Effects.DealDamage(7, treefolk)
            }
        }
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "161"
        artist = "Randy Gallegos"
        flavorText = "\"The elves use treefolk to drive us away. It is time to remove their tools.\"\n—Vessifrus, flamekin demagogue"
        imageUri = "https://cards.scryfall.io/normal/front/5/6/56a4ec08-5029-4f7b-a93b-a5dad4be5113.jpg?1783942878"
    }
}
