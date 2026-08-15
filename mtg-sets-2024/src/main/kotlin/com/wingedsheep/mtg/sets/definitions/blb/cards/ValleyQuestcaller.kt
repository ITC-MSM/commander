package com.wingedsheep.mtg.sets.definitions.blb.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Valley Questcaller
 * {1}{W}
 * Creature — Rabbit Warrior
 * 2/3
 *
 * Whenever one or more other Rabbits, Bats, Birds, and/or Mice you control
 * enter, scry 1.
 * Other Rabbits, Bats, Birds, and Mice you control get +1/+1.
 */
val ValleyQuestcaller = card("Valley Questcaller") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Rabbit Warrior"
    power = 2
    toughness = 3
    oracleText = "Whenever one or more other Rabbits, Bats, Birds, and/or Mice you control enter, scry 1.\nOther Rabbits, Bats, Birds, and Mice you control get +1/+1."

    val valleySubtypes = listOf(
        Subtype("Rabbit"), Subtype("Bat"), Subtype("Bird"), Subtype("Mouse")
    )
    val valleyCreatureFilter = GameObjectFilter.Creature.youControl().withAnyOfSubtypes(valleySubtypes)

    // Triggered: whenever one or more other matching creatures ETB under your control, scry 1.
    // Batched (CR 603.3b): simultaneous entries yield a single scry, and Questcaller's own
    // entry doesn't count ("other") — but it does trigger off others entering alongside it.
    triggeredAbility {
        trigger = Triggers.OneOrMorePermanentsEnter(valleyCreatureFilter, excludeSource = true)
        effect = Patterns.Library.scry(1)
    }

    // Static lord: other Rabbits, Bats, Birds, and Mice you control get +1/+1
    staticAbility {
        ability = ModifyStats(
            powerBonus = 1,
            toughnessBonus = 1,
            filter = GroupFilter(valleyCreatureFilter, excludeSelf = true)
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "36"
        artist = "Steve Prescott"
        flavorText = "\"No battle is fought, or won, alone.\""
        imageUri = "https://cards.scryfall.io/normal/front/b/a/ba629ca8-a368-4282-8a61-9bf6a5c217f0.jpg?1721639461"

        ruling("2024-07-26", "If Valley Questcaller enters at the same time as one or more other Rabbits, Bats, Birds, and/or Mice you control, its first ability will trigger.")
    }
}
