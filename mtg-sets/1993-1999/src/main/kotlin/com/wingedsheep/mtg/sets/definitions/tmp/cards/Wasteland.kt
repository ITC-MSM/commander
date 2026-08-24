package com.wingedsheep.mtg.sets.definitions.tmp.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetPermanent

/**
 * Wasteland
 * Land
 * {T}: Add {C}.
 * {T}, Sacrifice this land: Destroy target nonbasic land.
 */
val Wasteland = card("Wasteland") {
    colorIdentity = ""
    typeLine = "Land"
    oracleText = "{T}: Add {C}.\n" +
        "{T}, Sacrifice this land: Destroy target nonbasic land."

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddColorlessMana(1)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Tap, Costs.SacrificeSelf)
        val land = target("target", TargetPermanent(filter = TargetFilter.NonbasicLand))
        effect = Effects.Destroy(land)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "330"
        artist = "Una Fricker"
        flavorText = "\"The land promises nothing and keeps its promise.\"\n" +
            "—Oracle *en*-Vec"
        imageUri = "https://cards.scryfall.io/normal/front/9/9/99ff731b-8399-40c8-b539-ba6ba5783771.jpg"
    }
}
