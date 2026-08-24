package com.wingedsheep.mtg.sets.definitions.znr.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.AbilityCost
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.effects.AddManaEffect

/**
 * Brightclimb Pathway // Grimclimb Pathway — Zendikar Rising #259 (canonical printing)
 * Land // Land · Modal double-faced card
 *
 * Front — Brightclimb Pathway: "{T}: Add {W}."
 * Back  — Grimclimb Pathway: "{T}: Add {B}."
 *
 * One of the ten Pathways. Per CR 712.12 the card is played as a land with one of its faces
 * chosen on the way in, and it enters with that face up — so this is two mutually exclusive
 * land plays off a single card, not a dual land. The choice is final: CR 712.9 excludes modal
 * DFCs from transforming. See `RiverglidePathway` (Zendikar Rising) for the full note on how the
 * cycle is modelled.
 */
private val BrightclimbPathwayFront = card("Brightclimb Pathway") {
    typeLine = "Land"
    colorIdentity = "W"
    oracleText = "{T}: Add {W}."

    activatedAbility {
        cost = AbilityCost.Tap
        effect = AddManaEffect(Color.WHITE)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "259"
        artist = "Johannes Voss"
        imageUri = "https://cards.scryfall.io/normal/front/d/2/d24c3d51-795d-4c01-a34a-3280fccd2d78.jpg?1783929313"
    }
}

private val GrimclimbPathwayBack = card("Grimclimb Pathway") {
    typeLine = "Land"
    colorIdentity = "B"
    oracleText = "{T}: Add {B}."

    activatedAbility {
        cost = AbilityCost.Tap
        effect = AddManaEffect(Color.BLACK)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "259"
        artist = "Johannes Voss"
        imageUri = "https://cards.scryfall.io/normal/back/d/2/d24c3d51-795d-4c01-a34a-3280fccd2d78.jpg?1783929313"
    }
}

val BrightclimbPathway: CardDefinition = CardDefinition.modalDoubleFacedLand(
    frontFace = BrightclimbPathwayFront,
    backFace = GrimclimbPathwayBack,
)
