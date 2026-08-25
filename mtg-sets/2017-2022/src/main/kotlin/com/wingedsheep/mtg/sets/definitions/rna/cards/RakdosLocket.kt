package com.wingedsheep.mtg.sets.definitions.rna.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.TimingRule

/**
 * Rakdos Locket — Ravnica Allegiance #237
 * {3} · Artifact
 *
 * See [AzoriusLocket] for the cycle's wiring — two mana abilities plus the hybrid-cost draw.
 */
val RakdosLocket = card("Rakdos Locket") {
    manaCost = "{3}"
    colorIdentity = "BR"
    typeLine = "Artifact"
    oracleText = "{T}: Add {B} or {R}.\n" +
        "{B/R}{B/R}{B/R}{B/R}, {T}, Sacrifice this artifact: Draw two cards."

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
        cost = Costs.Composite(Costs.Mana("{B/R}{B/R}{B/R}{B/R}"), Costs.Tap, Costs.SacrificeSelf)
        effect = Effects.DrawCards(2)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "237"
        artist = "Sung Choi"
        flavorText = "\"This trinket will gain you admittance to some painfully exclusive gatherings.\"\n" +
        "—Exava, blood witch"
        imageUri = "https://cards.scryfall.io/normal/front/4/f/4fd60d9b-2282-4b32-9bff-efb2bcf87d22.jpg"
    }
}
