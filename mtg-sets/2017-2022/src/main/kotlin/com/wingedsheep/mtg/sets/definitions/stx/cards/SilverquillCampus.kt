package com.wingedsheep.mtg.sets.definitions.stx.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersTapped
import com.wingedsheep.sdk.scripting.TimingRule

/**
 * Silverquill Campus — Strixhaven: School of Mages #273 (canonical printing)
 * (no mana cost) · Land
 *
 * This land enters tapped.
 * {T}: Add {W} or {B}.
 * {4}, {T}: Scry 1. (Look at the top card of your library. You may put that card on the bottom.)
 *
 * The Campus cycle (see [PrismariCampus]): an unconditional [EntersTapped] plus "Add {W} or {B}"
 * written as two separate one-colour mana abilities — the player picks a colour by picking which
 * ability to activate — and a plain [Effects.Scry] behind the {4} activation.
 */
val SilverquillCampus = card("Silverquill Campus") {
    manaCost = ""
    colorIdentity = "BW"
    typeLine = "Land"
    oracleText =
        "This land enters tapped.\n" +
        "{T}: Add {W} or {B}.\n" +
        "{4}, {T}: Scry 1. (Look at the top card of your library. You may put that card on the bottom.)"

    replacementEffect(EntersTapped())

    // {T}: Add {W} or {B}. — modeled as one ability per colour.
    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddMana(Color.WHITE)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddMana(Color.BLACK)
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
        collectorNumber = "273"
        artist = "Titus Lunter"
        flavorText = "Mage-students drawn to the power of language choose Silverquill, the college of eloquence."
        imageUri = "https://cards.scryfall.io/normal/front/4/2/42583850-ba5c-4a71-8717-406b5c6d048f.jpg?1783927270"
    }
}
