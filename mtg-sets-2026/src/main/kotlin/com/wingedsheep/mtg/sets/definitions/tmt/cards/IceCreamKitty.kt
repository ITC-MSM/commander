package com.wingedsheep.mtg.sets.definitions.tmt.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.predicates.CardPredicate

/**
 * Ice Cream Kitty
 * {1}{B/G}
 * Artifact Creature — Food Cat Mutant
 * 1/3
 *
 * {2}, Sacrifice another creature or token: Draw a card. Activate only
 * as a sorcery.
 * {2}, {T}, Sacrifice this creature: You gain 3 life.
 */
val IceCreamKitty = card("Ice Cream Kitty") {
    manaCost = "{1}{B/G}"
    colorIdentity = "BG"
    typeLine = "Artifact Creature — Food Cat Mutant"
    oracleText = "{2}, Sacrifice another creature or token: Draw a card. Activate only as a sorcery.\n{2}, {T}, Sacrifice this creature: You gain 3 life."
    power = 1
    toughness = 3

    activatedAbility {
        cost = Costs.Composite(
            Costs.Mana("{2}"),
            Costs.SacrificeAnother(
                GameObjectFilter(
                    cardPredicates = listOf(
                        CardPredicate.Or(
                            listOf(
                                CardPredicate.IsCreature,
                                CardPredicate.IsToken,
                            )
                        )
                    )
                )
            )
        )
        timing = TimingRule.SorcerySpeed
        effect = Effects.DrawCards(1)
    }

    activatedAbility {
        cost = Costs.Composite(
            Costs.Mana("{2}"),
            Costs.Tap,
            Costs.SacrificeSelf
        )
        effect = Effects.GainLife(3)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "150"
        artist = "Maël Ollivier-Henry"
        flavorText = "\"That's not what they mean by 'pet food,' dude!\"\n—Michelangelo"
        imageUri = "https://cards.scryfall.io/normal/front/6/6/66081fd3-2457-4602-96c4-3075ddfec1c2.jpg?1771587014"
    }
}
