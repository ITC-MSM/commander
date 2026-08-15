package com.wingedsheep.mtg.sets.definitions.ltr.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.ForEachTargetEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Cast into the Fire
 * {1}{R}
 * Instant
 *
 * Choose one —
 * • Cast into the Fire deals 1 damage to each of up to two target creatures.
 * • Exile target artifact.
 */
val CastIntoTheFire = card("Cast into the Fire") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Instant"
    oracleText = "Choose one —\n• Cast into the Fire deals 1 damage to each of up to two target creatures.\n• Exile target artifact."

    spell {
        modal(chooseCount = 1) {
            mode("Cast into the Fire deals 1 damage to each of up to two target creatures") {
                target = TargetCreature(count = 2, optional = true)
                effect = ForEachTargetEffect(
                    effects = listOf(Effects.DealDamage(1, EffectTarget.ContextTarget(0)))
                )
            }
            mode("Exile target artifact") {
                val artifact = target("target artifact", Targets.Artifact)
                effect = Effects.Exile(artifact)
            }
        }
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "118"
        artist = "Aurore Folny"
        flavorText = "\"But for Gollum, I could not have destroyed the Ring. So let us forgive him! For the Quest is achieved, and now all is over.\"\n—Frodo"
        imageUri = "https://cards.scryfall.io/normal/front/2/e/2ef878cb-27b6-47d8-ad11-bd20529b0e7e.jpg?1686968832"
    }
}
