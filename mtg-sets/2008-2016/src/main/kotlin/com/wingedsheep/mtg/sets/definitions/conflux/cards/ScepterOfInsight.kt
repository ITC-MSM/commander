package com.wingedsheep.mtg.sets.definitions.conflux.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Scepter of Insight
 * {1}{U}{U}
 * Artifact
 * {3}{U}, {T}: Draw a card.
 *
 * A mana-plus-tap [Costs.Composite] over [Effects.DrawCards]; the draw's default target is the
 * ability's controller, which is exactly what the printed line means and what Assay's model
 * leaves unwritten.
 */
val ScepterOfInsight = card("Scepter of Insight") {
    manaCost = "{1}{U}{U}"
    colorIdentity = "U"
    typeLine = "Artifact"
    oracleText = "{3}{U}, {T}: Draw a card."

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{3}{U}"), Costs.Tap)
        effect = Effects.DrawCards(1)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "33"
        artist = "Steven Belledin"
        flavorText = "\"The road to truth has many branches, and so must the cane with which I walk it.\" —Voln the Elder"
        imageUri = "https://cards.scryfall.io/normal/front/8/a/8ac51021-e66c-4cd1-b54f-031b69d9699f.jpg"
    }
}
