package com.wingedsheep.mtg.sets.definitions.tmp.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Rootwater Diver
 * {U}
 * Creature — Merfolk
 * 1/1
 * {T}, Sacrifice this creature: Return target artifact card from your graveyard to your hand.
 */
val RootwaterDiver = card("Rootwater Diver") {
    manaCost = "{U}"
    colorIdentity = "U"
    typeLine = "Creature — Merfolk"
    power = 1
    toughness = 1
    oracleText = "{T}, Sacrifice this creature: Return target artifact card from your graveyard to your hand."

    activatedAbility {
        cost = Costs.Composite(Costs.Tap, Costs.SacrificeSelf)
        val artifact = target(
            "target artifact card from your graveyard",
            TargetObject(filter = TargetFilter.ArtifactInYourGraveyard)
        )
        effect = Effects.ReturnToHand(artifact)
        description = "{T}, Sacrifice this creature: Return target artifact card from your graveyard to your hand."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "81"
        artist = "Ron Spencer"
        flavorText = "\"Drop a dagger into the murky deep and the merfolk will cut your throat with it.\"\n" +
            "—Skyshroud saying"
        imageUri = "https://cards.scryfall.io/normal/front/a/6/a6315323-cf82-46c0-b164-e6ea1bf809f4.jpg"
    }
}
