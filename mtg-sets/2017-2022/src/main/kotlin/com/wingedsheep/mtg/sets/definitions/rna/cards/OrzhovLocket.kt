package com.wingedsheep.mtg.sets.definitions.rna.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.TimingRule

/**
 * Orzhov Locket — Ravnica Allegiance #236
 * {3} · Artifact
 *
 * See [AzoriusLocket] for the cycle's wiring — two mana abilities plus the hybrid-cost draw.
 */
val OrzhovLocket = card("Orzhov Locket") {
    manaCost = "{3}"
    colorIdentity = "BW"
    typeLine = "Artifact"
    oracleText = "{T}: Add {W} or {B}.\n" +
        "{W/B}{W/B}{W/B}{W/B}, {T}, Sacrifice this artifact: Draw two cards."

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
    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{W/B}{W/B}{W/B}{W/B}"), Costs.Tap, Costs.SacrificeSelf)
        effect = Effects.DrawCards(2)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "236"
        artist = "Volkan Baǵa"
        flavorText = "\"It looks expensive, doesn't it? You have no idea...\"\n" +
        "—Milana, Orzhov prelate"
        imageUri = "https://cards.scryfall.io/normal/front/7/6/761e7188-bad1-4775-84a2-15da9a42a57c.jpg"
    }
}
