package com.wingedsheep.mtg.sets.definitions.one.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Basilica Skullbomb
 * {1}
 * Artifact
 *
 * {1}, Sacrifice this artifact: Draw a card.
 * {2}{W}, Sacrifice this artifact: Target creature you control gets +2/+2 and gains flying until end of turn. Draw a card. Activate only as a sorcery.
 */
val BasilicaSkullbomb = card("Basilica Skullbomb") {
    manaCost = "{1}"
    colorIdentity = "W"
    typeLine = "Artifact"
    oracleText = "{1}, Sacrifice this artifact: Draw a card.\n" +
        "{2}{W}, Sacrifice this artifact: Target creature you control gets +2/+2 and gains flying until end of turn. Draw a card. Activate only as a sorcery."

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{1}"), Costs.SacrificeSelf)
        effect = Effects.DrawCards(1)
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{2}{W}"), Costs.SacrificeSelf)
        target = Targets.CreatureYouControl
        effect = Effects.Composite(
            Effects.ModifyStats(2, 2, EffectTarget.ContextTarget(0)),
            Effects.GrantKeyword(Keyword.FLYING, EffectTarget.ContextTarget(0)),
            Effects.DrawCards(1)
        )
        timing = TimingRule.SorcerySpeed
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "224"
        artist = "Gaboleps"
        imageUri = "https://cards.scryfall.io/normal/front/8/e/8e2f0ae2-db68-4338-93f9-9d9268cec41e.jpg?1783917994"
    }
}
