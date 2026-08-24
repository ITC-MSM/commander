package com.wingedsheep.mtg.sets.definitions.khm.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.AbilityCost
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.effects.AddManaEffect

/**
 * Barkchannel Pathway // Tidechannel Pathway — Kaldheim #251 (canonical printing)
 * Land // Land · Modal double-faced card
 *
 * Front — Barkchannel Pathway: "{T}: Add {G}."
 * Back  — Tidechannel Pathway: "{T}: Add {U}."
 *
 * One of the ten Pathways. Per CR 712.12 the card is played as a land with one of its faces
 * chosen on the way in, and it enters with that face up — so this is two mutually exclusive
 * land plays off a single card, not a dual land. The choice is final: CR 712.9 excludes modal
 * DFCs from transforming. See `RiverglidePathway` (Zendikar Rising) for the full note on how the
 * cycle is modelled.
 */
private val BarkchannelPathwayFront = card("Barkchannel Pathway") {
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
        collectorNumber = "251"
        artist = "Daniel Ljunggren"
        imageUri = "https://cards.scryfall.io/normal/front/b/6/b6de14ae-0132-4261-af00-630bf15918cd.jpg?1783928184"
    }
}

private val TidechannelPathwayBack = card("Tidechannel Pathway") {
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
        collectorNumber = "251"
        artist = "Daniel Ljunggren"
        imageUri = "https://cards.scryfall.io/normal/back/b/6/b6de14ae-0132-4261-af00-630bf15918cd.jpg?1783928184"
    }
}

val BarkchannelPathway: CardDefinition = CardDefinition.modalDoubleFacedLand(
    frontFace = BarkchannelPathwayFront,
    backFace = TidechannelPathwayBack,
)
