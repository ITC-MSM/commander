package com.wingedsheep.mtg.sets.definitions.znr.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.AbilityCost
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.effects.AddManaEffect

/**
 * Cragcrown Pathway // Timbercrown Pathway — Zendikar Rising #261 (canonical printing)
 * Land // Land · Modal double-faced card
 *
 * Front — Cragcrown Pathway: "{T}: Add {R}."
 * Back  — Timbercrown Pathway: "{T}: Add {G}."
 *
 * One of the ten Pathways. Per CR 712.12 the card is played as a land with one of its faces
 * chosen on the way in, and it enters with that face up — so this is two mutually exclusive
 * land plays off a single card, not a dual land. The choice is final: CR 712.9 excludes modal
 * DFCs from transforming. See `RiverglidePathway` (Zendikar Rising) for the full note on how the
 * cycle is modelled.
 */
private val CragcrownPathwayFront = card("Cragcrown Pathway") {
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
        collectorNumber = "261"
        artist = "Andreas Rocha"
        imageUri = "https://cards.scryfall.io/normal/front/d/a/da57eb54-5199-4a56-95f7-f6ac432876b1.jpg?1783929311"
    }
}

private val TimbercrownPathwayBack = card("Timbercrown Pathway") {
    typeLine = "Land"
    colorIdentity = "G"
    oracleText = "{T}: Add {G}."

    activatedAbility {
        cost = AbilityCost.Tap
        effect = AddManaEffect(Color.GREEN)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "261"
        artist = "Andreas Rocha"
        imageUri = "https://cards.scryfall.io/normal/back/d/a/da57eb54-5199-4a56-95f7-f6ac432876b1.jpg?1783929311"
    }
}

val CragcrownPathway: CardDefinition = CardDefinition.modalDoubleFacedLand(
    frontFace = CragcrownPathwayFront,
    backFace = TimbercrownPathwayBack,
)
