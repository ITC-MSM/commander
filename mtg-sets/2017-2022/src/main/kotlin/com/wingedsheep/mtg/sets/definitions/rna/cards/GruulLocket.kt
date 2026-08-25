package com.wingedsheep.mtg.sets.definitions.rna.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.TimingRule

/**
 * Gruul Locket — Ravnica Allegiance #234
 * {3} · Artifact
 *
 * See [AzoriusLocket] for the cycle's wiring — two mana abilities plus the hybrid-cost draw.
 */
val GruulLocket = card("Gruul Locket") {
    manaCost = "{3}"
    colorIdentity = "GR"
    typeLine = "Artifact"
    oracleText = "{T}: Add {R} or {G}.\n" +
        "{R/G}{R/G}{R/G}{R/G}, {T}, Sacrifice this artifact: Draw two cards."

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
        cost = Costs.Composite(Costs.Mana("{R/G}{R/G}{R/G}{R/G}"), Costs.Tap, Costs.SacrificeSelf)
        effect = Effects.DrawCards(2)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "234"
        artist = "Kev Walker"
        flavorText = "\"In life, it was a cunning survivor, fearless and quick. May its power pass to you as you wear its skull.\"\n" +
        "—Gna, Gruul shaman"
        imageUri = "https://cards.scryfall.io/normal/front/1/e/1ec78880-a8ec-4c87-bc3f-e2a79d154884.jpg"
    }
}
