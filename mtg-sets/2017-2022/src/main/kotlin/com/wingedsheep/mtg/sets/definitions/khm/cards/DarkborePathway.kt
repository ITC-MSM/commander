package com.wingedsheep.mtg.sets.definitions.khm.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.AbilityCost
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.effects.AddManaEffect

/**
 * Darkbore Pathway // Slitherbore Pathway — Kaldheim #254 (canonical printing)
 * Land // Land · Modal double-faced card
 *
 * Front — Darkbore Pathway: "{T}: Add {B}."
 * Back  — Slitherbore Pathway: "{T}: Add {G}."
 *
 * One of the ten Pathways. Per CR 712.12 the card is played as a land with one of its faces
 * chosen on the way in, and it enters with that face up — so this is two mutually exclusive
 * land plays off a single card, not a dual land. The choice is final: CR 712.9 excludes modal
 * DFCs from transforming. See `RiverglidePathway` (Zendikar Rising) for the full note on how the
 * cycle is modelled.
 */
private val DarkborePathwayFront = card("Darkbore Pathway") {
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
        collectorNumber = "254"
        artist = "Johannes Voss"
        imageUri = "https://cards.scryfall.io/normal/front/8/7/87a4e5fe-161f-42da-9ca2-67c8e8970e94.jpg?1783928184"
    }
}

private val SlitherborePathwayBack = card("Slitherbore Pathway") {
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
        collectorNumber = "254"
        artist = "Johannes Voss"
        imageUri = "https://cards.scryfall.io/normal/back/8/7/87a4e5fe-161f-42da-9ca2-67c8e8970e94.jpg?1783928184"
    }
}

val DarkborePathway: CardDefinition = CardDefinition.modalDoubleFacedLand(
    frontFace = DarkborePathwayFront,
    backFace = SlitherborePathwayBack,
)
