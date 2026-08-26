package com.wingedsheep.mtg.sets.definitions.plc.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Healing Leaves
 * {G}
 * Instant
 * Choose one —
 * • Target player gains 3 life.
 * • Prevent the next 3 damage that would be dealt to any target this turn.
 */
val HealingLeaves = card("Healing Leaves") {
    manaCost = "{G}"
    colorIdentity = "G"
    typeLine = "Instant"
    oracleText = "Choose one —\n" +
        "• Target player gains 3 life.\n" +
        "• Prevent the next 3 damage that would be dealt to any target this turn."

    spell {
        modal(chooseCount = 1) {
            mode("Target player gains 3 life") {
                val p = target("target", Targets.Player)
                effect = Effects.GainLife(3, p)
            }
            mode("Prevent the next 3 damage that would be dealt to any target this turn") {
                val t = target("target", Targets.Any)
                effect = Effects.PreventNextDamage(3, t)
            }
        }
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "150"
        artist = "Michael Sutfin"
        flavorText = "The elves of Llanowar don't trust in alchemy. They rely instead on pure herbs harvested from the forest's sacred heart."
        imageUri = "https://cards.scryfall.io/normal/front/d/6/d6f58258-a1bd-4364-954e-c39938f7dab2.jpg"
    }
}
