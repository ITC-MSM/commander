package com.wingedsheep.mtg.sets.definitions.ala.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersTapped
import com.wingedsheep.sdk.scripting.TimingRule

/**
 * Savage Lands
 * Land
 * This land enters tapped.
 * {T}: Add {B}, {R}, or {G}.
 *
 * One of the Alara "tri-lands": no basic land types, so the three mana abilities are authored
 * explicitly rather than derived from the type line.
 */
val SavageLands = card("Savage Lands") {
    manaCost = ""
    colorIdentity = "BRG"
    typeLine = "Land"
    oracleText = "This land enters tapped.\n{T}: Add {B}, {R}, or {G}."

    replacementEffect(EntersTapped())

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddMana(Color.BLACK)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }
    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddMana(Color.RED)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }
    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddMana(Color.GREEN)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "228"
        artist = "Vance Kovacs"
        flavorText = "Jund is a world as cruel as those who call it home. Their brutal struggles scar the land even as it carves them in its image, a vicious circle spiraling out of control."
        imageUri = "https://cards.scryfall.io/normal/front/f/6/f6bcded3-6eea-4533-a855-e03d4c4620d6.jpg"
    }
}
