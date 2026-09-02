package com.wingedsheep.mtg.sets.definitions.stx.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Tangletrap — Strixhaven: School of Mages #145 (canonical printing)
 * {1}{G} · Instant
 *
 * Choose one —
 * • Tangletrap deals 5 damage to target creature with flying.
 * • Destroy target artifact.
 *
 * Each mode declares its own target, so the mode is chosen first and only that mode's target is
 * announced (CR 601.2b) — picking the artifact mode never requires a legal flyer. The first mode is
 * [Effects.DealDamage] over [Targets.CreatureWithKeyword]`(FLYING)`, the second [Effects.Destroy]
 * over [Targets.Artifact].
 */
val Tangletrap = card("Tangletrap") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Instant"
    oracleText =
        "Choose one —\n" +
        "• Tangletrap deals 5 damage to target creature with flying.\n" +
        "• Destroy target artifact."

    spell {
        modal {
            mode("Tangletrap deals 5 damage to target creature with flying.") {
                val flyer = target("target", Targets.CreatureWithKeyword(Keyword.FLYING))
                effect = Effects.DealDamage(5, flyer)
            }
            mode("Destroy target artifact.") {
                val artifact = target("target", Targets.Artifact)
                effect = Effects.Destroy(artifact)
            }
        }
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "145"
        artist = "Suzanne Helmigh"
        flavorText = "\"It's like that old saying: what goes up . . . feeds the arboretum.\"\n—Lisette, Witherbloom dean"
        imageUri = "https://cards.scryfall.io/normal/front/8/0/80fbf729-00c0-4237-8294-c857f96364d3.jpg?1783927337"
    }
}
