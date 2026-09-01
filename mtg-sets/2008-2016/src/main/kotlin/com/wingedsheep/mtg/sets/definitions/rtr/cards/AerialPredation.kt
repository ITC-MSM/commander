package com.wingedsheep.mtg.sets.definitions.rtr.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Aerial Predation
 * {2}{G}
 * Instant
 *
 * Destroy target creature with flying. You gain 2 life.
 *
 * Canonical printing: Return to Ravnica, the card's earliest real printing.
 *
 * Destroy-plus-rider: a [Effects.Composite] of [Effects.Destroy] on the bound target and an
 * untargeted [Effects.GainLife]. "With flying" is a keyword predicate on the target filter
 * ([Targets.CreatureWithKeyword]), which reads projected state, so a creature that only has
 * flying from a continuous effect is a legal target.
 */
val AerialPredation = card("Aerial Predation") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Instant"
    oracleText = "Destroy target creature with flying. You gain 2 life."

    spell {
        val t = target("target creature with flying", Targets.CreatureWithKeyword(Keyword.FLYING))
        effect = Effects.Composite(
            Effects.Destroy(t),
            Effects.GainLife(2),
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "113"
        artist = "BD"
        flavorText = "In the towering trees of the Samok Stand and the predators that guard them, the might of the Ravnican wild has returned."
        imageUri = "https://cards.scryfall.io/normal/front/e/c/ec3c023c-037e-495a-b7df-32be42a75f36.jpg?1783940351"
    }
}
