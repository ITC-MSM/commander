package com.wingedsheep.mtg.sets.definitions.neo.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Repel the Vile — Kamigawa: Neon Dynasty #33 (canonical printing)
 * {3}{W} · Instant
 *
 * Choose one —
 * • Exile target creature with power 4 or greater.
 * • Exile target enchantment.
 *
 * Each mode declares its own target, so the mode is chosen first and only that mode's target is
 * announced (CR 601.2b) — picking the enchantment mode never requires a legal big creature.
 */
val RepelTheVile = card("Repel the Vile") {
    manaCost = "{3}{W}"
    colorIdentity = "W"
    typeLine = "Instant"
    oracleText = "Choose one —\n• Exile target creature with power 4 or greater.\n• Exile target enchantment."

    spell {
        modal {
            mode("Exile target creature with power 4 or greater.") {
                val t = target(
                    "big creature",
                    TargetObject(filter = TargetFilter(GameObjectFilter.Creature.powerAtLeast(4))),
                )
                effect = Effects.Exile(t)
            }
            mode("Exile target enchantment.") {
                val t = target(
                    "enchantment",
                    TargetObject(filter = TargetFilter(GameObjectFilter.Enchantment)),
                )
                effect = Effects.Exile(t)
            }
        }
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "33"
        artist = "Dominik Mayer"
        flavorText = "\"You are a guest in this realm, and you've overstayed your welcome.\"\n" +
            "—Yui, Imperial intercessor"
        imageUri = "https://cards.scryfall.io/normal/front/9/a/9a37d5b4-af3a-48b0-895f-91b652108ef6.jpg?1783923914"
    }
}
