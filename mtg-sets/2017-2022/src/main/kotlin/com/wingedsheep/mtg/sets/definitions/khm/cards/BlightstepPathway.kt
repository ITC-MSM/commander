package com.wingedsheep.mtg.sets.definitions.khm.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.AbilityCost
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.effects.AddManaEffect

/**
 * Blightstep Pathway // Searstep Pathway — Kaldheim #252 (canonical printing)
 * Land // Land · Modal double-faced card
 *
 * Front — Blightstep Pathway: "{T}: Add {B}."
 * Back  — Searstep Pathway: "{T}: Add {R}."
 *
 * One of the ten Pathways. Per CR 712.12 the card is played as a land with one of its faces
 * chosen on the way in, and it enters with that face up — so this is two mutually exclusive
 * land plays off a single card, not a dual land. The choice is final: CR 712.9 excludes modal
 * DFCs from transforming. See `RiverglidePathway` (Zendikar Rising) for the full note on how the
 * cycle is modelled.
 */
private val BlightstepPathwayFront = card("Blightstep Pathway") {
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
        collectorNumber = "252"
        artist = "Ravenna Tran"
        imageUri = "https://cards.scryfall.io/normal/front/0/c/0ce39a19-f51d-4a35-ae80-5b82eb15fcff.jpg?1783928185"
    }
}

private val SearstepPathwayBack = card("Searstep Pathway") {
    typeLine = "Land"
    colorIdentity = "R"
    oracleText = "{T}: Add {R}."

    activatedAbility {
        cost = AbilityCost.Tap
        effect = AddManaEffect(Color.RED)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "252"
        artist = "Ravenna Tran"
        imageUri = "https://cards.scryfall.io/normal/back/0/c/0ce39a19-f51d-4a35-ae80-5b82eb15fcff.jpg?1783928185"
    }
}

val BlightstepPathway: CardDefinition = CardDefinition.modalDoubleFacedLand(
    frontFace = BlightstepPathwayFront,
    backFace = SearstepPathwayBack,
)
