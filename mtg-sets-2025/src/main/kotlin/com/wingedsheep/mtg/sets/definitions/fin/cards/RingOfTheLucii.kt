package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetPermanent


/**
 * Ring of the Lucii
 * {4}
 * Legendary Artifact
 * {T}: Add {C}{C}.
 * {2}, {T}, Pay 1 life: Tap target nonland permanent.
 */
val RingOfTheLucii = card("Ring of the Lucii") {
    manaCost = "{4}"
    colorIdentity = ""
    typeLine = "Legendary Artifact"
    oracleText = "{T}: Add {C}{C}.\n{2}, {T}, Pay 1 life: Tap target nonland permanent."
    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddColorlessMana(2)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }
    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{2}"), Costs.Tap, Costs.PayLife(1))
        val t = target("target", TargetPermanent(filter = TargetFilter.NonlandPermanent))
        effect = Effects.Tap(t)
    }
    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "269"
        artist = "Lorenzo Mastroianni"
        flavorText = "\"Kings of Lucis... come to me!\"\n—Noctis Lucis Caelum"
        imageUri = "https://cards.scryfall.io/normal/front/7/5/75761f1e-9449-4c58-8265-8abac71dafc1.jpg"
    }
}
