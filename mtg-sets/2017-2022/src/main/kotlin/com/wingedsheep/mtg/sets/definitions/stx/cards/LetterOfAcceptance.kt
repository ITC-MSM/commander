package com.wingedsheep.mtg.sets.definitions.stx.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.TimingRule

/**
 * Letter of Acceptance — Strixhaven: School of Mages #256 (canonical printing)
 * {3} · Artifact
 *
 * {T}: Add one mana of any color.
 * {2}, {T}, Sacrifice this artifact: Draw a card.
 *
 * A mana rock in the Guild Globe shape: a five-colour mana ability via [Effects.AddManaOfChoice],
 * and a cash-out whose cost is a [Costs.Composite] of mana, tap and [Costs.SacrificeSelf] paying
 * for a plain [Effects.DrawCards].
 */
val LetterOfAcceptance = card("Letter of Acceptance") {
    manaCost = "{3}"
    colorIdentity = ""
    typeLine = "Artifact"
    oracleText =
        "{T}: Add one mana of any color.\n" +
        "{2}, {T}, Sacrifice this artifact: Draw a card."

    // {T}: Add one mana of any color.
    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddManaOfChoice()
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    // {2}, {T}, Sacrifice this artifact: Draw a card.
    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{2}"), Costs.Tap, Costs.SacrificeSelf)
        effect = Effects.DrawCards(1)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "256"
        artist = "Daniel Ljunggren"
        flavorText = "The letter unfolded, inviting the twins to Strixhaven. Will saw a chance for arcane study. Rowan saw a chance for power."
        imageUri = "https://cards.scryfall.io/normal/front/3/4/34750231-34aa-401c-b192-47b56588923a.jpg?1783927280"
    }
}
