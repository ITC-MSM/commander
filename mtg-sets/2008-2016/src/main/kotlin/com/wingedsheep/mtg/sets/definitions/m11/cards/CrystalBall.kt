package com.wingedsheep.mtg.sets.definitions.m11.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Crystal Ball
 * {3}
 * Artifact
 *
 * {1}, {T}: Scry 2. (Look at the top two cards of your library, then put any number of them on the
 * bottom and the rest on top in any order.)
 *
 * One activated ability: a two-part cost ([Costs.Mana] plus [Costs.Tap]) over the [Effects.Scry]
 * facade — the `Patterns.Library.scry` composition behind it — never a hand-rolled
 * look-and-reorder pipeline. The reminder text in parentheses is scry's own definition, so it adds
 * nothing to the script.
 */
val CrystalBall = card("Crystal Ball") {
    manaCost = "{3}"
    colorIdentity = ""
    typeLine = "Artifact"
    oracleText = "{1}, {T}: Scry 2. (Look at the top two cards of your library, then put any number of them on the bottom and the rest on top in any order.)"

    activatedAbility {
        cost = Costs.Composite(
            Costs.Mana("{1}"),
            Costs.Tap,
        )
        effect = Effects.Scry(2)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "203"
        artist = "Ron Spencer"
        flavorText = "It glints with arcane truths to those who know how to glimpse them."
        imageUri = "https://cards.scryfall.io/normal/front/5/6/56dc98fb-a956-46f7-aca2-97929b4236ee.jpg?1783941791"
    }
}
