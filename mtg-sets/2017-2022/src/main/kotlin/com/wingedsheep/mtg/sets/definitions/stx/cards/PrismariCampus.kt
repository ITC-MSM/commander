package com.wingedsheep.mtg.sets.definitions.stx.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersTapped
import com.wingedsheep.sdk.scripting.TimingRule

/**
 * Prismari Campus
 *
 * Land
 * This land enters tapped.
 * {T}: Add {U} or {R}.
 * {4}, {T}: Scry 1. (Look at the top card of your library. You may put that card on the bottom.)
 *
 * The Campus cycle is the Coastal Tower shape: an unconditional [EntersTapped] plus "Add {U} or
 * {R}" written as two separate one-colour mana abilities, which is how the SDK models a choice of
 * colours — the player picks by choosing which ability to activate. The type line is bare `Land`
 * with no basic land subtypes, so these written abilities are the land's only mana. The cycling
 * payoff is plain [Effects.Scry].
 */
val PrismariCampus = card("Prismari Campus") {
    manaCost = ""
    colorIdentity = "RU"
    typeLine = "Land"
    oracleText = "This land enters tapped.\n" +
        "{T}: Add {U} or {R}.\n" +
        "{4}, {T}: Scry 1. (Look at the top card of your library. You may put that card on the bottom.)"

    replacementEffect(EntersTapped())

    // {T}: Add {U} or {R}. — modeled as one ability per colour.
    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddMana(Color.BLUE)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddMana(Color.RED)
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
        collectorNumber = "270"
        artist = "Adam Paquette"
        flavorText = "Mage-students who see spellcraft as the highest form of expression choose Prismari, the college of elemental arts."
        imageUri = "https://cards.scryfall.io/normal/front/7/6/768120f5-9401-4e52-924e-3374bde65b3d.jpg?1783927271"
    }
}
