package com.wingedsheep.mtg.sets.definitions.znr.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.AbilityCost
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.effects.AddManaEffect

/**
 * Needleverge Pathway // Pillarverge Pathway — Zendikar Rising #263 (canonical printing)
 * Land // Land · Modal double-faced card
 *
 * Front — Needleverge Pathway: "{T}: Add {R}."
 * Back  — Pillarverge Pathway: "{T}: Add {W}."
 *
 * One of the ten Pathways. Per CR 712.12 the card is played as a land with one of its faces
 * chosen on the way in, and it enters with that face up — so this is two mutually exclusive
 * land plays off a single card, not a dual land. The choice is final: CR 712.9 excludes modal
 * DFCs from transforming. See `RiverglidePathway` (Zendikar Rising) for the full note on how the
 * cycle is modelled.
 */
private val NeedlevergePathwayFront = card("Needleverge Pathway") {
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
        collectorNumber = "263"
        artist = "Piotr Dura"
        imageUri = "https://cards.scryfall.io/normal/front/6/5/6559047e-6ede-4815-a3a0-389062094f9d.jpg?1783929311"
    }
}

private val PillarvergePathwayBack = card("Pillarverge Pathway") {
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
        collectorNumber = "263"
        artist = "Piotr Dura"
        imageUri = "https://cards.scryfall.io/normal/back/6/5/6559047e-6ede-4815-a3a0-389062094f9d.jpg?1783929311"
    }
}

val NeedlevergePathway: CardDefinition = CardDefinition.modalDoubleFacedLand(
    frontFace = NeedlevergePathwayFront,
    backFace = PillarvergePathwayBack,
)
