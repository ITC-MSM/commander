package com.wingedsheep.mtg.sets.definitions.war.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Casualties of War
 * {2}{B}{B}{G}{G}
 * Sorcery
 *
 * Choose one or more —
 * • Destroy target artifact.
 * • Destroy target creature.
 * • Destroy target enchantment.
 * • Destroy target land.
 * • Destroy target planeswalker.
 *
 * "Choose one or more" is `minChooseCount = 1` against a `chooseCount` equal to the number of
 * modes: every mode may be taken, and at least one must be.
 */
val CasualtiesOfWar = card("Casualties of War") {
    manaCost = "{2}{B}{B}{G}{G}"
    colorIdentity = "BG"
    typeLine = "Sorcery"
    oracleText = "Choose one or more —\n" +
        "• Destroy target artifact.\n" +
        "• Destroy target creature.\n" +
        "• Destroy target enchantment.\n" +
        "• Destroy target land.\n" +
        "• Destroy target planeswalker."

    spell {
        modal(chooseCount = 5, minChooseCount = 1) {
            mode("Destroy target artifact") {
                val t = target("target artifact", Targets.Artifact)
                effect = Effects.Destroy(t)
            }
            mode("Destroy target creature") {
                val t = target("target creature", Targets.Creature)
                effect = Effects.Destroy(t)
            }
            mode("Destroy target enchantment") {
                val t = target("target enchantment", Targets.Enchantment)
                effect = Effects.Destroy(t)
            }
            mode("Destroy target land") {
                val t = target("target land", Targets.Land)
                effect = Effects.Destroy(t)
            }
            mode("Destroy target planeswalker") {
                val t = target("target planeswalker", Targets.Planeswalker)
                effect = Effects.Destroy(t)
            }
        }
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "187"
        artist = "Tomasz Jedruszek"
        imageUri = "https://cards.scryfall.io/normal/front/0/8/08fc5e50-c6f7-41ec-815a-5667eefded78.jpg?1783933402"
    }
}
