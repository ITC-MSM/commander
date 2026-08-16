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
 * Maze Skullbomb
 * {1}
 * Artifact
 *
 * {1}, Sacrifice this artifact: Draw a card.
 * {2}{G}, Sacrifice this artifact: Target creature you control gets +3/+3 and gains trample until end of turn. Draw a card. Activate only as a sorcery.
 */
val MazeSkullbomb = card("Maze Skullbomb") {
    manaCost = "{1}"
    colorIdentity = "G"
    typeLine = "Artifact"
    oracleText = "{1}, Sacrifice this artifact: Draw a card.\n" +
        "{2}{G}, Sacrifice this artifact: Target creature you control gets +3/+3 and gains trample until end of turn. Draw a card. Activate only as a sorcery."

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{1}"), Costs.SacrificeSelf)
        effect = Effects.DrawCards(1)
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{2}{G}"), Costs.SacrificeSelf)
        target = Targets.CreatureYouControl
        effect = Effects.Composite(
            Effects.ModifyStats(3, 3, EffectTarget.ContextTarget(0)),
            Effects.GrantKeyword(Keyword.TRAMPLE, EffectTarget.ContextTarget(0)),
            Effects.DrawCards(1)
        )
        timing = TimingRule.SorcerySpeed
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "231"
        artist = "Matt Forsyth"
        imageUri = "https://cards.scryfall.io/normal/front/1/0/1073aee2-aea6-473d-97c6-248778d79d80.jpg?1783917990"
    }
}
