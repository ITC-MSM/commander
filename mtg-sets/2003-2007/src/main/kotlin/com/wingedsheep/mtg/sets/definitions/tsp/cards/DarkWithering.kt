package com.wingedsheep.mtg.sets.definitions.tsp.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.madness
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Dark Withering
 * {4}{B}{B}
 * Instant
 * Destroy target nonblack creature.
 * Madness {B} (If you discard this card, discard it into exile. When you do, cast it for its
 * madness cost or put it into your graveyard.)
 *
 * "Nonblack" is `CardPredicate.NotColor(BLACK)`, which reads the creature's colors — a
 * multicolored creature that is partly black is spared. Madness is display-only at the DSL
 * layer; both halves live in the engine and key off the `KeywordAbility.Madness` entry.
 */
val DarkWithering = card("Dark Withering") {
    manaCost = "{4}{B}{B}"
    colorIdentity = "B"
    typeLine = "Instant"
    oracleText = "Destroy target nonblack creature.\n" +
        "Madness {B} (If you discard this card, discard it into exile. When you do, cast it for its madness cost or put it into your graveyard.)"

    spell {
        val t = target(
            "target",
            TargetObject(filter = TargetFilter(GameObjectFilter.Creature.notColor(Color.BLACK)))
        )
        effect = Effects.Destroy(t)
    }

    madness("{B}")

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "101"
        artist = "Wayne Reynolds"
        imageUri = "https://cards.scryfall.io/normal/front/3/d/3da58e0d-5877-43c4-b129-993e154b6087.jpg"
    }
}
