package com.wingedsheep.mtg.sets.definitions.stx.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersTapped
import com.wingedsheep.sdk.scripting.TimingRule

/**
 * Witherbloom Campus — Strixhaven: School of Mages #275 (canonical printing)
 * (no mana cost) · Land
 *
 * This land enters tapped.
 * {T}: Add {B} or {G}.
 * {4}, {T}: Scry 1. (Look at the top card of your library. You may put that card on the bottom.)
 *
 * The Campus cycle (see [PrismariCampus]): an unconditional [EntersTapped] plus "Add {B} or {G}"
 * written as two separate one-colour mana abilities — the player picks a colour by picking which
 * ability to activate — and a plain [Effects.Scry] behind the {4} activation.
 */
val WitherbloomCampus = card("Witherbloom Campus") {
    manaCost = ""
    colorIdentity = "BG"
    typeLine = "Land"
    oracleText =
        "This land enters tapped.\n" +
        "{T}: Add {B} or {G}.\n" +
        "{4}, {T}: Scry 1. (Look at the top card of your library. You may put that card on the bottom.)"

    replacementEffect(EntersTapped())

    // {T}: Add {B} or {G}. — modeled as one ability per colour.
    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddMana(Color.BLACK)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddMana(Color.GREEN)
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
        collectorNumber = "275"
        artist = "Alayna Danner"
        flavorText = "Mage-students fascinated by the energies of life and death choose Witherbloom, the college of essence studies."
        imageUri = "https://cards.scryfall.io/normal/front/7/3/7346fb2e-754e-47de-b33d-eb089b357ee4.jpg?1783927269"
    }
}
