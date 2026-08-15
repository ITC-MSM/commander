package com.wingedsheep.mtg.sets.definitions.sos.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Quandrix Charm
 * {G}{U}
 * Instant
 * Choose one —
 * • Counter target spell unless its controller pays {2}.
 * • Destroy target enchantment.
 * • Target creature has base power and toughness 5/5 until end of turn.
 *
 * The standard `modal(chooseCount = 1)` charm shape (sibling of Witherbloom / Prismari).
 * Mode 1 is the Mana Leak idiom (`Effects.CounterUnlessPays("{2}")`), mode 2 a flat destroy on a
 * target enchantment, mode 3 sets a creature's base power and toughness to 5/5 until end of turn
 * (`Effects.SetBasePowerAndToughness`, Layer 7b set value).
 */
val QuandrixCharm = card("Quandrix Charm") {
    manaCost = "{G}{U}"
    colorIdentity = "UG"
    typeLine = "Instant"
    oracleText = "Choose one —\n" +
        "• Counter target spell unless its controller pays {2}.\n" +
        "• Destroy target enchantment.\n" +
        "• Target creature has base power and toughness 5/5 until end of turn."

    spell {
        modal(chooseCount = 1) {
            mode("Counter target spell unless its controller pays {2}") {
                target = Targets.Spell
                effect = Effects.CounterUnlessPays("{2}")
            }
            mode("Destroy target enchantment") {
                val t = target("target enchantment", Targets.Enchantment)
                effect = Effects.Destroy(t)
            }
            mode("Target creature has base power and toughness 5/5 until end of turn") {
                val t = target("target creature", Targets.Creature)
                effect = Effects.SetBasePowerAndToughness(5, 5, t)
            }
        }
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "217"
        artist = "Matheus Graef"
        imageUri = "https://cards.scryfall.io/normal/front/3/1/318486e0-f255-40f5-8150-dc272eec9d7d.jpg?1775938509"
    }
}
