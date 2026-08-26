package com.wingedsheep.mtg.sets.definitions.ths.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CanOnlyBlockCreaturesWith
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Vaporkin
 * {1}{U}
 * Creature — Elemental
 * 2 / 1
 *
 * Flying
 * This creature can block only creatures with flying.
 *
 * The blocking restriction is a printed static on the creature itself, so
 * [CanOnlyBlockCreaturesWith]'s `filter` default (`GroupFilter.source()`) is the right one — only an
 * Equipment or Aura would have to pass `GroupFilter.attachedCreature()` instead. Welkin Tern shape.
 */
val Vaporkin = card("Vaporkin") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Elemental"
    power = 2
    toughness = 1
    oracleText = "Flying\nThis creature can block only creatures with flying."

    keywords(Keyword.FLYING)

    staticAbility {
        ability = CanOnlyBlockCreaturesWith(GameObjectFilter.Creature.withKeyword(Keyword.FLYING))
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "72"
        artist = "Seb McKinnon"
        flavorText = "\"Mists are carefree. They drift where they will, unencumbered by rocks and river beds.\"\n—Thrasios, triton hero"
        imageUri = "https://cards.scryfall.io/normal/front/0/9/09b661a5-359b-4f21-b1b6-aa0988810b4d.jpg"
    }
}
