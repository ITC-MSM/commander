package com.wingedsheep.mtg.sets.definitions.znr.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.AbilityCost
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.effects.AddManaEffect

/**
 * Riverglide Pathway // Lavaglide Pathway — Zendikar Rising #264 (canonical printing)
 * Land // Land · Modal double-faced card
 *
 * Front — Riverglide Pathway: "{T}: Add {U}."
 * Back  — Lavaglide Pathway:  "{T}: Add {R}."
 *
 * The Pathway cycle's whole trick is CR 712.12: *"A player playing a modal double-faced card as a
 * land chooses one of its faces that's a land before putting it onto the battlefield. It enters
 * the battlefield with that face up."* So this is not a dual land and not a land that transforms —
 * it is one card offering **two different land plays**, and the choice is made on the way in and
 * is permanent. Once it's on the battlefield it has only the played face's characteristics
 * (CR 712.8f), and it can never turn over (CR 712.9 excludes modal DFCs from transforming).
 *
 * That is why each face is an ordinary one-colour tapland-less land with a single mana ability,
 * and why they are joined by [CardDefinition.modalDoubleFacedLand] rather than by a dual's two
 * mana abilities: the engine's land enumerator offers one play per land *face*, so both names
 * appear in hand and the player picks the colour they need this turn.
 */
private val RiverglidePathwayFront = card("Riverglide Pathway") {
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
        collectorNumber = "264"
        artist = "Kieran Yanner"
        imageUri = "https://cards.scryfall.io/normal/front/2/6/2668ac91-6cda-4f81-a08d-4fc5f9cb35b2.jpg?1783929311"
    }
}

private val LavaglidePathwayBack = card("Lavaglide Pathway") {
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
        collectorNumber = "264"
        artist = "Kieran Yanner"
        imageUri = "https://cards.scryfall.io/normal/back/2/6/2668ac91-6cda-4f81-a08d-4fc5f9cb35b2.jpg?1783929311"
    }
}

val RiverglidePathway: CardDefinition = CardDefinition.modalDoubleFacedLand(
    frontFace = RiverglidePathwayFront,
    backFace = LavaglidePathwayBack,
)
