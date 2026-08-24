package com.wingedsheep.mtg.sets.definitions.m15.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Tyrant's Machine
 * {2}
 * Artifact
 * {4}, {T}: Tap target creature.
 */
val TyrantsMachine = card("Tyrant's Machine") {
    manaCost = "{2}"
    typeLine = "Artifact"
    oracleText = "{4}, {T}: Tap target creature."

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{4}"), Costs.Tap)
        val t = target("target creature", Targets.Creature)
        effect = Effects.Tap(t)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "238"
        artist = "Yeong-Hao Han"
        flavorText = "\"Though tempered differently, all wills can be broken.\"\n—Inquisitor Kyrik"
        imageUri = "https://cards.scryfall.io/normal/front/0/8/088045cd-8aec-43ff-bb8f-d17927b79cfb.jpg?1783939153"
    }
}
