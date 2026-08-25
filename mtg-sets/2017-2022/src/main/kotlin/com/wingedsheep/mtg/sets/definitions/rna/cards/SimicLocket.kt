package com.wingedsheep.mtg.sets.definitions.rna.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.TimingRule

/**
 * Simic Locket — Ravnica Allegiance #240
 * {3} · Artifact
 *
 * See [AzoriusLocket] for the cycle's wiring — two mana abilities plus the hybrid-cost draw.
 */
val SimicLocket = card("Simic Locket") {
    manaCost = "{3}"
    colorIdentity = "GU"
    typeLine = "Artifact"
    oracleText = "{T}: Add {G} or {U}.\n" +
        "{G/U}{G/U}{G/U}{G/U}, {T}, Sacrifice this artifact: Draw two cards."

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
    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{G/U}{G/U}{G/U}{G/U}"), Costs.Tap, Costs.SacrificeSelf)
        effect = Effects.DrawCards(2)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "240"
        artist = "Yeong-Hao Han"
        flavorText = "\"Like our guild itself, this locket can stand for many things. You must discern what it means for you.\"\n" +
        "—Vannifar"
        imageUri = "https://cards.scryfall.io/normal/front/2/9/29c65978-e5b0-428e-aace-f99768ca6106.jpg"
    }
}
