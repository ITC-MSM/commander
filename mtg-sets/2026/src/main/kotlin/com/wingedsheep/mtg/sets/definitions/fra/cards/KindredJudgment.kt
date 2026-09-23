package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.OptionType
import com.wingedsheep.sdk.scripting.predicates.CardPredicate

private const val CHOSEN_TYPE = "chosenCreatureType"

val KindredJudgment = card("Kindred Judgment") {
    manaCost = "{5}{W}{W}"
    colorIdentity = "W"
    typeLine = "Sorcery"
    oracleText = "Choose a creature type. Destroy all creatures that aren't of the chosen type."

    // The type is chosen on resolution; the destroy set reads projected subtypes, so a
    // changeling (every creature type) is always spared.
    spell {
        effect = Effects.Composite(
            Effects.ChooseOption(OptionType.CREATURE_TYPE, storeAs = CHOSEN_TYPE),
            Effects.DestroyAll(
                GameObjectFilter.Creature.copy(
                    cardPredicates = GameObjectFilter.Creature.cardPredicates +
                        CardPredicate.Not(CardPredicate.HasSubtypeFromVariable(CHOSEN_TYPE))
                )
            )
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "13"
        artist = "Ekaterina Burmak"
        flavorText = "In the end, the light shone, the land flourished, and the dead and vile were washed away."
        imageUri = "https://cards.scryfall.io/normal/front/6/f/6f9f814b-8249-4e48-a05e-4c84060fe6fb.jpg?1789470776"
        inBooster = false
    }
}
