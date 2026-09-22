package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ActivationRestriction
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetPermanent

val RescueGirlFirstResponder = card("Rescue Girl, First Responder") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Legendary Creature — Human Cleric"
    oracleText = "Flying\n" +
        "{T}: Return another target permanent you control to its owner's hand. Activate only during your turn."
    power = 1
    toughness = 3

    keywords(Keyword.FLYING)

    activatedAbility {
        cost = Costs.Tap
        restrictions = listOf(ActivationRestriction.OnlyDuringYourTurn)
        val permanent = target(
            "another target permanent you control",
            TargetPermanent(filter = TargetFilter.Permanent.youControl().other()),
        )
        effect = Effects.ReturnToHand(permanent)
        description = "{T}: Return another target permanent you control to its owner's hand."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "202"
        artist = "Cristi Balanescu"
        flavorText = "\"The Helpful Hero! The Daring Darling of the Tenth District!\"\n—*Ravnica Gazette* headline"
        imageUri = "https://cards.scryfall.io/normal/front/6/9/699874e3-1ccf-4a6c-8371-61040de82d08.jpg?1789645598"
        inBooster = false
    }
}
