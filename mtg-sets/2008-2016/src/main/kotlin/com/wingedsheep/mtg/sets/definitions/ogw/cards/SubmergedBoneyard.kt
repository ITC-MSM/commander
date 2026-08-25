package com.wingedsheep.mtg.sets.definitions.ogw.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersTapped
import com.wingedsheep.sdk.scripting.TimingRule

/**
 * Submerged Boneyard
 * Land
 * This land enters tapped.
 * {T}: Add {U} or {B}.
 */
val SubmergedBoneyard = card("Submerged Boneyard") {
    colorIdentity = "BU"
    typeLine = "Land"
    oracleText = "This land enters tapped.\n" +
        "{T}: Add {U} or {B}."

    replacementEffect(EntersTapped())

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddMana(Color.BLUE)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddMana(Color.BLACK)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "178"
        artist = "Cliff Childs"
        flavorText = "\"Long after the land has given up the last of its secrets, there will still be mysteries in the depths of the sea.\"\n" +
            "—Kiora"
        imageUri = "https://cards.scryfall.io/normal/front/6/4/6479a246-21ac-490a-aefb-6ed72aabdb88.jpg"
    }
}
