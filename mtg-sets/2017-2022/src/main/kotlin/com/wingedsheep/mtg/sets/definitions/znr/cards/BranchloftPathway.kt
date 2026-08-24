package com.wingedsheep.mtg.sets.definitions.znr.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.AbilityCost
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.effects.AddManaEffect

/**
 * Branchloft Pathway // Boulderloft Pathway — Zendikar Rising #258 (canonical printing)
 * Land // Land · Modal double-faced card
 *
 * Front — Branchloft Pathway: "{T}: Add {G}."
 * Back  — Boulderloft Pathway: "{T}: Add {W}."
 *
 * One of the ten Pathways. Per CR 712.12 the card is played as a land with one of its faces
 * chosen on the way in, and it enters with that face up — so this is two mutually exclusive
 * land plays off a single card, not a dual land. The choice is final: CR 712.9 excludes modal
 * DFCs from transforming. See `RiverglidePathway` (Zendikar Rising) for the full note on how the
 * cycle is modelled.
 */
private val BranchloftPathwayFront = card("Branchloft Pathway") {
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
        collectorNumber = "258"
        artist = "Titus Lunter"
        imageUri = "https://cards.scryfall.io/normal/front/0/5/0511e232-2a72-40f5-a400-4f7ebc442d17.jpg?1783929314"
    }
}

private val BoulderloftPathwayBack = card("Boulderloft Pathway") {
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
        collectorNumber = "258"
        artist = "Titus Lunter"
        imageUri = "https://cards.scryfall.io/normal/back/0/5/0511e232-2a72-40f5-a400-4f7ebc442d17.jpg?1783929314"
    }
}

val BranchloftPathway: CardDefinition = CardDefinition.modalDoubleFacedLand(
    frontFace = BranchloftPathwayFront,
    backFace = BoulderloftPathwayBack,
)
