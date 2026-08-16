package com.wingedsheep.mtg.sets.definitions.one.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Dross Skullbomb
 * {1}
 * Artifact
 *
 * {1}, Sacrifice this artifact: Draw a card.
 * {2}{B}, Sacrifice this artifact: Return target creature card from your graveyard to your hand. Draw a card. Activate only as a sorcery.
 */
val DrossSkullbomb = card("Dross Skullbomb") {
    manaCost = "{1}"
    colorIdentity = "B"
    typeLine = "Artifact"
    oracleText = "{1}, Sacrifice this artifact: Draw a card.\n" +
        "{2}{B}, Sacrifice this artifact: Return target creature card from your graveyard to your hand. Draw a card. Activate only as a sorcery."

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{1}"), Costs.SacrificeSelf)
        effect = Effects.DrawCards(1)
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{2}{B}"), Costs.SacrificeSelf)
        target = Targets.CreatureCardInYourGraveyard
        effect = Effects.Composite(
            Effects.ReturnToHand(EffectTarget.ContextTarget(0)),
            Effects.DrawCards(1)
        )
        timing = TimingRule.SorcerySpeed
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "225"
        artist = "Gaboleps"
        imageUri = "https://cards.scryfall.io/normal/front/6/6/66270dd2-9139-4329-9621-852962836688.jpg?1783917994"
    }
}
