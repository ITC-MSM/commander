package com.wingedsheep.mtg.sets.definitions.khm.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.AbilityCost
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.effects.AddManaEffect

/**
 * Hengegate Pathway // Mistgate Pathway — Kaldheim #260 (canonical printing)
 * Land // Land · Modal double-faced card
 *
 * Front — Hengegate Pathway: "{T}: Add {W}."
 * Back  — Mistgate Pathway: "{T}: Add {U}."
 *
 * One of the ten Pathways. Per CR 712.12 the card is played as a land with one of its faces
 * chosen on the way in, and it enters with that face up — so this is two mutually exclusive
 * land plays off a single card, not a dual land. The choice is final: CR 712.9 excludes modal
 * DFCs from transforming. See `RiverglidePathway` (Zendikar Rising) for the full note on how the
 * cycle is modelled.
 */
private val HengegatePathwayFront = card("Hengegate Pathway") {
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
        collectorNumber = "260"
        artist = "Yeong-Hao Han"
        imageUri = "https://cards.scryfall.io/normal/front/7/e/7ef37cb3-d803-47d7-8a01-9c803aa2eadc.jpg?1783928182"
    }
}

private val MistgatePathwayBack = card("Mistgate Pathway") {
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
        artist = "Yeong-Hao Han"
        imageUri = "https://cards.scryfall.io/normal/back/7/e/7ef37cb3-d803-47d7-8a01-9c803aa2eadc.jpg?1783928182"
    }
}

val HengegatePathway: CardDefinition = CardDefinition.modalDoubleFacedLand(
    frontFace = HengegatePathwayFront,
    backFace = MistgatePathwayBack,
)
