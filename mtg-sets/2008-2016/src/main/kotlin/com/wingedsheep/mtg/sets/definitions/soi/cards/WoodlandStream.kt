package com.wingedsheep.mtg.sets.definitions.soi.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersTapped
import com.wingedsheep.sdk.scripting.TimingRule

/**
 * Woodland Stream
 * Land
 * This land enters tapped.
 * {T}: Add {G} or {U}.
 *
 * "Add {G} or {U}" is two separate mana abilities rather than one choosing effect — that is the
 * corpus shape for every dual land, and it is what makes each colour individually enumerable
 * during mana payment.
 */
val WoodlandStream = card("Woodland Stream") {
    manaCost = ""
    colorIdentity = "GU"
    typeLine = "Land"
    oracleText = "This land enters tapped.\n" +
        "{T}: Add {G} or {U}."

    replacementEffect(EntersTapped())

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddMana(Color.GREEN)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }
    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddMana(Color.BLUE)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "282"
        artist = "James Paick"
        flavorText = "Two creaking waterwheels herald the approach to Briarbridge through the Ulvenwald."
        imageUri = "https://cards.scryfall.io/normal/front/1/8/18a0c745-dee6-4cb2-acaa-3d2b8e0cce5b.jpg?1783937694"
    }
}
