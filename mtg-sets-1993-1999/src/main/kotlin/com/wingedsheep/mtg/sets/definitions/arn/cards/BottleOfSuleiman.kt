package com.wingedsheep.mtg.sets.definitions.arn.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.CreateTokenEffect
import com.wingedsheep.sdk.scripting.effects.DealDamageEffect
import com.wingedsheep.sdk.scripting.effects.FlipCoinEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Bottle of Suleiman
 * {4}
 * Artifact
 * {1}, Sacrifice this artifact: Flip a coin. If you win the flip, create a 5/5 colorless
 * Djinn artifact creature token with flying. If you lose the flip, this artifact deals 5 damage to you.
 */
val BottleOfSuleiman = card("Bottle of Suleiman") {
    manaCost = "{4}"
    typeLine = "Artifact"
    oracleText = "{1}, Sacrifice this artifact: Flip a coin. If you win the flip, create a 5/5 colorless Djinn artifact creature token with flying. If you lose the flip, this artifact deals 5 damage to you."

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{1}"), Costs.SacrificeSelf)
        effect = FlipCoinEffect(
            wonEffect = CreateTokenEffect(
                power = 5,
                toughness = 5,
                colors = emptySet(),
                creatureTypes = setOf("Djinn"),
                keywords = setOf(Keyword.FLYING),
                artifactToken = true,
            ),
            lostEffect = DealDamageEffect(5, EffectTarget.Controller),
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "58"
        artist = "Jesper Myrfors"
        imageUri = "https://cards.scryfall.io/normal/front/c/4/c474cd6b-5610-49eb-ac98-918d900efe8b.jpg?1562931775"
    }
}
