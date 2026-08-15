package com.wingedsheep.mtg.sets.definitions.spm.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.CounterEffect
import com.wingedsheep.sdk.scripting.effects.DrawCardsEffect
import com.wingedsheep.sdk.scripting.targets.TargetSpell

/**
 * School Daze
 * {3}{U}{U}
 * Instant
 * Choose one —
 * • Do Homework — Draw three cards.
 * • Fight Crime — Counter target spell. Draw a card.
 */
val SchoolDaze = card("School Daze") {
    manaCost = "{3}{U}{U}"
    colorIdentity = "U"
    typeLine = "Instant"
    oracleText = "Choose one —\n• Do Homework — Draw three cards.\n• Fight Crime — Counter target spell. Draw a card."
    spell {
        modal(chooseCount = 1) {
            mode("Do Homework — Draw three cards") {
                effect = DrawCardsEffect(3)
            }
            mode("Fight Crime — Counter target spell. Draw a card") {
                val t = target("target", TargetSpell())
                effect = Effects.Composite(
                    CounterEffect(),
                    DrawCardsEffect(1)
                )
            }
        }
    }
    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "42"
        artist = "Domenico Cava"
        flavorText = "\"Peter possesses a brilliant scientific mind. It's a shame he can't grasp being on time.\"\n—Dr. Curt Connors"
        imageUri = "https://cards.scryfall.io/normal/front/e/5/e5b61b9d-31a2-4e09-9612-4980bf8de708.jpg?1757377011"
    }
}
