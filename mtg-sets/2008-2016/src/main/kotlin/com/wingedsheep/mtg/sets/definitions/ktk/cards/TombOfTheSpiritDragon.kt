package com.wingedsheep.mtg.sets.definitions.ktk.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.AbilityCost
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.effects.AddColorlessManaEffect
import com.wingedsheep.sdk.scripting.predicates.CardPredicate
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Tomb of the Spirit Dragon
 * Land
 * {T}: Add {C}.
 * {2}, {T}: You gain 1 life for each colorless creature you control.
 */
val TombOfTheSpiritDragon = card("Tomb of the Spirit Dragon") {
    typeLine = "Land"
    colorIdentity = ""
    oracleText = "{T}: Add {C}.\n{2}, {T}: You gain 1 life for each colorless creature you control."

    activatedAbility {
        cost = AbilityCost.Tap
        effect = AddColorlessManaEffect(1)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{2}"), Costs.Tap)
        effect = Effects.GainLife(
            DynamicAmount.AggregateBattlefield(
                player = Player.You,
                filter = GameObjectFilter(
                    cardPredicates = listOf(CardPredicate.IsCreature, CardPredicate.IsColorless)
                ).youControl()
            )
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "245"
        artist = "Sam Burley"
        flavorText = "\"The voice calls me here, yet I see only bones. Is this more dragon trickery?\" —Sarkhan Vol"
        imageUri = "https://cards.scryfall.io/normal/front/e/b/eb6be7ab-8fad-4606-b623-f2188219d60b.jpg?1562795503"
        ruling("2014-09-20", "Count the number of colorless creatures you control (including face-down creatures) as the last ability resolves to determine how much life you gain.")
    }
}
