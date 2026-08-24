package com.wingedsheep.mtg.sets.definitions.znr.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.AbilityCost
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.effects.AddManaEffect

/**
 * Clearwater Pathway // Murkwater Pathway — Zendikar Rising #260 (canonical printing)
 * Land // Land · Modal double-faced card
 *
 * Front — Clearwater Pathway: "{T}: Add {U}."
 * Back  — Murkwater Pathway: "{T}: Add {B}."
 *
 * One of the ten Pathways. Per CR 712.12 the card is played as a land with one of its faces
 * chosen on the way in, and it enters with that face up — so this is two mutually exclusive
 * land plays off a single card, not a dual land. The choice is final: CR 712.9 excludes modal
 * DFCs from transforming. See `RiverglidePathway` (Zendikar Rising) for the full note on how the
 * cycle is modelled.
 */
private val ClearwaterPathwayFront = card("Clearwater Pathway") {
    typeLine = "Land"
    colorIdentity = "U"
    oracleText = "{T}: Add {U}."

    activatedAbility {
        cost = AbilityCost.Tap
        effect = AddManaEffect(Color.BLUE)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "260"
        artist = "Daarken"
        imageUri = "https://cards.scryfall.io/normal/front/b/4/b4b99ebb-0d54-4fe5-a495-979aaa564aa8.jpg?1783929316"
    }
}

private val MurkwaterPathwayBack = card("Murkwater Pathway") {
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
        collectorNumber = "260"
        artist = "Daarken"
        imageUri = "https://cards.scryfall.io/normal/back/b/4/b4b99ebb-0d54-4fe5-a495-979aaa564aa8.jpg?1783929316"
    }
}

val ClearwaterPathway: CardDefinition = CardDefinition.modalDoubleFacedLand(
    frontFace = ClearwaterPathwayFront,
    backFace = MurkwaterPathwayBack,
)
