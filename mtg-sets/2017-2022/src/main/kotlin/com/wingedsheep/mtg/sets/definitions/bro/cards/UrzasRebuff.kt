package com.wingedsheep.mtg.sets.definitions.bro.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Urza's Rebuff
 * {1}{U}{U}
 * Instant
 * Choose one —
 * • Counter target spell.
 * • Tap up to two target creatures.
 */
val UrzasRebuff = card("Urza's Rebuff") {
    manaCost = "{1}{U}{U}"
    colorIdentity = "U"
    typeLine = "Instant"
    oracleText = "Choose one —\n• Counter target spell.\n• Tap up to two target creatures."

    spell {
        modal(chooseCount = 1) {
            mode("Counter target spell") {
                target("target", Targets.Spell)
                effect = Effects.CounterSpell()
            }
            mode("Tap up to two target creatures") {
                target("target", TargetCreature(count = 2, optional = true))
                effect = Effects.TapEachTarget()
            }
        }
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "71"
        artist = "Josu Hernaiz"
        flavorText = "\"As usual, my brother's maneuvers are brash and impulsive.\"\n—Urza"
        imageUri = "https://cards.scryfall.io/normal/front/8/a/8a6f90b3-450c-490a-b6d0-2393c4646c85.jpg?1783920103"
    }
}
