package com.wingedsheep.mtg.sets.definitions.som.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Salvage Scout
 * {W}
 * Creature — Human Scout
 * 1/1
 *
 * {W}, Sacrifice this creature: Return target artifact card from your graveyard to your hand.
 */
val SalvageScout = card("Salvage Scout") {
    manaCost = "{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Scout"
    power = 1
    toughness = 1
    oracleText = "{W}, Sacrifice this creature: Return target artifact card from your graveyard to your hand."

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{W}"), Costs.SacrificeSelf)
        val artifact = target("target", TargetObject(filter = TargetFilter.ArtifactInYourGraveyard))
        // Plain `ReturnToHand`, not the `FromGraveyard` variant: this is a *targeted* return, so
        // the requirement's own `zone = GRAVEYARD` is re-checked at resolution (CR 608.2b) and the
        // ability fizzles if the card has left. The `fromZone` guard belongs on self-returns, which
        // have no requirement to re-check.
        effect = Effects.ReturnToHand(artifact)
        description = "{W}, Sacrifice this creature: Return target artifact card from your graveyard to your hand."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "19"
        artist = "Randis Albion"
        flavorText = "\"I'm not saying it's dangerous work. I'm just saying don't sign up if you have plans for your seventieth birthday.\""
        imageUri = "https://cards.scryfall.io/normal/front/5/9/5909e77e-a930-4713-bca4-c6b265238c17.jpg?1783941742"
    }
}
