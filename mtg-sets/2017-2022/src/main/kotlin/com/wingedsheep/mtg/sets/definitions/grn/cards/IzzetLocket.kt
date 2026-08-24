package com.wingedsheep.mtg.sets.definitions.grn.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.TimingRule

/**
 * Izzet Locket
 * {3}
 * Artifact
 * {T}: Add {U} or {R}.
 * {U/R}{U/R}{U/R}{U/R}, {T}, Sacrifice this artifact: Draw two cards.
 */
val IzzetLocket = card("Izzet Locket") {
    manaCost = "{3}"
    colorIdentity = "RU"
    typeLine = "Artifact"
    oracleText = "{T}: Add {U} or {R}.\n" +
        "{U/R}{U/R}{U/R}{U/R}, {T}, Sacrifice this artifact: Draw two cards."

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
    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{U/R}{U/R}{U/R}{U/R}"), Costs.Tap, Costs.SacrificeSelf)
        effect = Effects.DrawCards(2)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "238"
        artist = "Dmitry Burmak"
        flavorText = "\"Remember to discharge your locket every seven hours. Unless you prefer the spontaneous aether overload, of course.\"\n—Daxiver, Izzet electromancer"
        imageUri = "https://cards.scryfall.io/normal/front/3/4/348f3bec-ad16-4bc7-8da9-3956c9900f95.jpg?1783934106"
    }
}
