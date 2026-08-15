package com.wingedsheep.mtg.sets.definitions.eoe.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Survey Mechan
 * {4}
 * Artifact Creature — Robot
 * 1/3
 * Flying
 * Hexproof
 * {10}, Sacrifice this creature: It deals 3 damage to any target. Target player draws three cards
 * and gains 3 life. This ability costs {X} less to activate, where X is the number of differently
 * named lands you control.
 */
val SurveyMechan = card("Survey Mechan") {
    manaCost = "{4}"
    colorIdentity = ""
    typeLine = "Artifact Creature — Robot"
    power = 1
    toughness = 3
    oracleText = "Flying\n" +
        "Hexproof (This creature can't be the target of spells or abilities your opponents control.)\n" +
        "{10}, Sacrifice this creature: It deals 3 damage to any target. Target player draws three cards and gains 3 life. This ability costs {X} less to activate, where X is the number of differently named lands you control."

    keywords(Keyword.FLYING, Keyword.HEXPROOF)

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{10}"), Costs.SacrificeSelf)
        val damageTarget = target("any target", Targets.Any)
        val drawTarget = target("target player", Targets.Player)
        effect = Effects.Composite(
            Effects.DealDamage(3, damageTarget),
            Effects.DrawCards(3, drawTarget),
            Effects.GainLife(3, drawTarget),
        )
        genericCostReduction = DynamicAmounts.differentlyNamedLandsYouControl()
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "245"
        artist = "Johann Bodin"
        imageUri = "https://cards.scryfall.io/normal/front/9/b/9b4278ea-6cd8-45ad-b024-daf3dedd29e0.jpg?1752947557"
        ruling(
            "2025-07-25",
            "Once you determine the cost to activate Survey Mechan's last ability, you may activate mana abilities to pay that cost. If the number of differently named lands you control changes while activating mana abilities (probably because you sacrificed one or more lands), the cost to activate the ability remains what you previously determined."
        )
        ruling(
            "2025-07-25",
            "To determine the number of differently named lands you control, count each land you control once, but only if its English name isn't exactly the same as another land you've already counted this way."
        )
    }
}
