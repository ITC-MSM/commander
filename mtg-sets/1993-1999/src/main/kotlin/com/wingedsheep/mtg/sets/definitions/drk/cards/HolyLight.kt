package com.wingedsheep.mtg.sets.definitions.drk.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Holy Light
 * {2}{W}
 * Instant
 * Nonwhite creatures get -1/-1 until end of turn.
 *
 * A group pump over every creature that isn't white — `CardPredicate.NotColor` reads the
 * creature's colors, so a multicolored creature that is partly white is spared.
 */
val HolyLight = card("Holy Light") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Instant"
    oracleText = "Nonwhite creatures get -1/-1 until end of turn."

    spell {
        effect = Effects.ForEachInGroup(
            GroupFilter(GameObjectFilter.Creature.notColor(Color.WHITE)),
            Effects.ModifyStats(-1, -1, EffectTarget.Self)
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "10"
        artist = "Drew Tucker"
        flavorText = "\"Bathed in hallowed light, the infidels looked upon the impurities of their souls and despaired.\" —*The Book of Tal*"
        imageUri = "https://cards.scryfall.io/normal/front/c/3/c3c8a850-bc99-4679-a316-45ecdea696b2.jpg?1783947948"
    }
}
