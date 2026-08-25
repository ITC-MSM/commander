package com.wingedsheep.mtg.sets.definitions.leg.cards

import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ActivationRestriction
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Hell's Caretaker
 * {3}{B}
 * Creature — Horror
 * 1/1
 *
 * {T}, Sacrifice a creature: Return target creature card from your graveyard to the battlefield. Activate only during your upkeep.
 */
val HellsCaretaker = card("Hell's Caretaker") {
    manaCost = "{3}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Horror"
    power = 1
    toughness = 1
    oracleText = "{T}, Sacrifice a creature: Return target creature card from your graveyard to the battlefield. " +
        "Activate only during your upkeep."

    activatedAbility {
        cost = Costs.Composite(Costs.Tap, Costs.Sacrifice(GameObjectFilter.Creature))
        restrictions = listOf(
            ActivationRestriction.All(
                ActivationRestriction.OnlyDuringYourTurn,
                ActivationRestriction.DuringStep(Step.UPKEEP),
            ),
        )
        val creatureCard = target(
            "target creature card from your graveyard",
            Targets.CreatureCardInYourGraveyard,
        )
        effect = Effects.PutOntoBattlefieldFromGraveyard(creatureCard)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "104"
        artist = "Sandra Everingham"
        flavorText = "You might leave here, Chenndra, should another take your place . . . ."
        imageUri = "https://cards.scryfall.io/normal/front/3/3/336b3b8f-d104-4f06-ad4f-c92b8a9038ca.jpg?1783948066"
    }
}
