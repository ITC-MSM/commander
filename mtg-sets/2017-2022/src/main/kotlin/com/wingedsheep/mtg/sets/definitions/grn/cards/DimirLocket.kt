package com.wingedsheep.mtg.sets.definitions.grn.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.TimingRule

/**
 * Dimir Locket
 * {3}
 * Artifact
 * {T}: Add {U} or {B}.
 * {U/B}{U/B}{U/B}{U/B}, {T}, Sacrifice this artifact: Draw two cards.
 */
val DimirLocket = card("Dimir Locket") {
    manaCost = "{3}"
    colorIdentity = "BU"
    typeLine = "Artifact"
    oracleText = "{T}: Add {U} or {B}.\n" +
        "{U/B}{U/B}{U/B}{U/B}, {T}, Sacrifice this artifact: Draw two cards."

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddMana(Color.BLUE)
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
        cost = Costs.Composite(Costs.Mana("{U/B}{U/B}{U/B}{U/B}"), Costs.Tap, Costs.SacrificeSelf)
        effect = Effects.DrawCards(2)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "234"
        artist = "Zezhou Chen"
        flavorText = "\"Wear this, and take your place among the shadows—wise, lethal, and unseen.\"\n—Ivrelya, Dimir spymaster"
        imageUri = "https://cards.scryfall.io/normal/front/d/f/dfb6810b-9bc9-43c5-8cd9-817b12ee3110.jpg?1783934108"
    }
}
