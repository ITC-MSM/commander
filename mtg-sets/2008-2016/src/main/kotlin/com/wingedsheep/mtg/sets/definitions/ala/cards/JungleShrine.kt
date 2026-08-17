package com.wingedsheep.mtg.sets.definitions.ala.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersTapped
import com.wingedsheep.sdk.scripting.TimingRule

/**
 * Jungle Shrine
 * Land
 * This land enters tapped.
 * {T}: Add {R}, {G}, or {W}.
 *
 * One of the Alara "tri-lands": no basic land types, so the three mana abilities are authored
 * explicitly rather than derived from the type line.
 */
val JungleShrine = card("Jungle Shrine") {
    manaCost = ""
    colorIdentity = "WRG"
    typeLine = "Land"
    oracleText = "This land enters tapped.\n{T}: Add {R}, {G}, or {W}."

    replacementEffect(EntersTapped())

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
    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddMana(Color.WHITE)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "226"
        artist = "Wayne Reynolds"
        flavorText = "On Naya, ambition and treachery are scarce, hunted nearly to extinction by the awe owed to terrestrial gods."
        imageUri = "https://cards.scryfall.io/normal/front/e/4/e4f0d262-cbfe-42d2-9066-0b48a38995d6.jpg"
    }
}
