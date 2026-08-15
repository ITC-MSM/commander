package com.wingedsheep.mtg.sets.definitions.ltr.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ConditionalStaticAbility
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.conditions.Exists
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.references.Player

/**
 * Snarling Warg
 * {3}{B}
 * Creature — Wolf
 * 3/4
 *
 * Menace
 * As long as you control a Goblin or Orc, this creature gets +1/+0.
 */
val SnarlingWarg = card("Snarling Warg") {
    manaCost = "{3}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Wolf"
    power = 3
    toughness = 4
    oracleText = "Menace (This creature can't be blocked except by two or more creatures.)\nAs long as you control a Goblin or Orc, this creature gets +1/+0."

    keywords(Keyword.MENACE)

    staticAbility {
        ability = ConditionalStaticAbility(
            ability = ModifyStats(1, 0, GroupFilter.source()),
            condition = Exists(
                player = Player.You,
                zone = Zone.BATTLEFIELD,
                filter = GameObjectFilter.Creature.withAnyOfSubtypes(
                    listOf(Subtype.GOBLIN, Subtype.ORC)
                )
            )
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "109"
        artist = "Tomas Duchek"
        flavorText = "A shuddering howl broke from him, as if he were a captain summoning his pack to the assault."
        imageUri = "https://cards.scryfall.io/normal/front/a/1/a1ad561c-ccb4-480a-88a4-0555d0ef245e.jpg?1686968733"
    }
}
