package com.wingedsheep.mtg.sets.definitions.stx.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersTapped
import com.wingedsheep.sdk.scripting.TimingRule

/**
 * Quandrix Campus — Strixhaven: School of Mages #271 (canonical printing)
 * (no mana cost) · Land
 *
 * This land enters tapped.
 * {T}: Add {G} or {U}.
 * {4}, {T}: Scry 1. (Look at the top card of your library. You may put that card on the bottom.)
 *
 * The Campus cycle (see [PrismariCampus]): an unconditional [EntersTapped] plus "Add {G} or {U}"
 * written as two separate one-colour mana abilities — the player picks a colour by picking which
 * ability to activate — and a plain [Effects.Scry] behind the {4} activation.
 */
val QuandrixCampus = card("Quandrix Campus") {
    manaCost = ""
    colorIdentity = "GU"
    typeLine = "Land"
    oracleText =
        "This land enters tapped.\n" +
        "{T}: Add {G} or {U}.\n" +
        "{4}, {T}: Scry 1. (Look at the top card of your library. You may put that card on the bottom.)"

    replacementEffect(EntersTapped())

    // {T}: Add {G} or {U}. — modeled as one ability per colour.
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

    // {4}, {T}: Scry 1.
    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{4}"), Costs.Tap)
        effect = Effects.Scry(1)
        description = "{4}, {T}: Scry 1."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "271"
        artist = "Piotr Dura"
        flavorText = "Mage-students who see the beauty in patterns and equations choose Quandrix, the college of numeromancy."
        imageUri = "https://cards.scryfall.io/normal/front/f/7/f788da28-481b-41fa-a70c-b53db6b0f068.jpg?1783927273"
    }
}
