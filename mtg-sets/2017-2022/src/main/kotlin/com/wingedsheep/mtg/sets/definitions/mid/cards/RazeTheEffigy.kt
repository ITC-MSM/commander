package com.wingedsheep.mtg.sets.definitions.mid.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Raze the Effigy
 * {R}
 * Instant
 * Choose one —
 * • Destroy target artifact.
 * • Target attacking creature gets +2/+2 until end of turn.
 *
 * Each mode carries its own target, so the mode chosen on announcement decides what is targeted —
 * the +2/+2 mode is only castable while something is attacking.
 */
val RazeTheEffigy = card("Raze the Effigy") {
    manaCost = "{R}"
    colorIdentity = "R"
    typeLine = "Instant"
    oracleText = "Choose one —\n• Destroy target artifact.\n• Target attacking creature gets +2/+2 until end of turn."

    spell {
        modal(chooseCount = 1) {
            mode("Destroy target artifact") {
                val t = target("target", Targets.Artifact)
                effect = Effects.Destroy(t)
            }
            mode("Target attacking creature gets +2/+2 until end of turn") {
                val t = target("target", TargetCreature(filter = TargetFilter.AttackingCreature))
                effect = Effects.ModifyStats(2, 2, t)
            }
        }
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "156"
        artist = "Cristi Balanescu"
        flavorText = "The folk of Kessig build up their courage by burning effigies of the things they fear."
        imageUri = "https://cards.scryfall.io/normal/front/e/0/e0dd378c-5050-48c9-9a16-6d91afa62d21.jpg?1783925591"
    }
}
