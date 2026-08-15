package com.wingedsheep.mtg.sets.definitions.dsk.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.CardLayout
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.conditions.Compare
import com.wingedsheep.sdk.scripting.conditions.ComparisonOperator
import com.wingedsheep.sdk.scripting.effects.SearchDestination
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Central Elevator // Promising Stairs (DSK 44) — split-layout Room (CR 709.5).
 *
 * Central Elevator {3}{U} — Enchantment — Room
 *   When you unlock this door, search your library for a Room card that doesn't have the same
 *   name as a Room you control, reveal it, put it into your hand, then shuffle.
 *
 * Promising Stairs {2}{U} — Enchantment — Room
 *   At the beginning of your upkeep, surveil 1. You win the game if there are eight or more
 *   different names among unlocked doors of Rooms you control.
 *
 * Cast each half separately; the cast face enters unlocked, the other locked. Pay the locked
 * face's printed mana cost as a sorcery-speed special action to unlock it (CR 709.5e).
 *
 * Notes:
 * - Central Elevator's search restriction is the new [GameObjectFilter.nameNotSharedWithControlledRoom]
 *   predicate. Per the 2024-09-20 ruling, only the names of *unlocked* doors of Rooms you control
 *   count; a split Room card in the library shares a name if either of its door names matches.
 * - Promising Stairs's win is a state-triggered ability (CR 603.8), checked continuously rather
 *   than only at upkeep — it triggers the moment you reach eight or more different unlocked-door
 *   names. The ruling clarifies it counts names, not permanents (a Room with two unlocked doors
 *   contributes two), which is exactly [DynamicAmount.UnlockedDoors] with `distinctNames = true`.
 */
val CentralElevatorPromisingStairs = card("Central Elevator // Promising Stairs") {
    layout = CardLayout.SPLIT
    colorIdentity = "U"

    face("Central Elevator") {
        manaCost = "{3}{U}"
        typeLine = "Enchantment — Room"
        oracleText = "When you unlock this door, search your library for a Room card that doesn't " +
            "have the same name as a Room you control, reveal it, put it into your hand, then shuffle."

        triggeredAbility {
            trigger = Triggers.OnDoorUnlocked
            effect = Patterns.Library.searchLibrary(
                filter = GameObjectFilter.Any
                    .withSubtype(Subtype.ROOM)
                    .nameNotSharedWithControlledRoom(),
                count = 1,
                destination = SearchDestination.HAND,
                reveal = true,
                shuffleAfter = true,
            )
            description = "When you unlock this door, search your library for a Room card that " +
                "doesn't have the same name as a Room you control, reveal it, put it into your " +
                "hand, then shuffle."
        }
    }

    face("Promising Stairs") {
        manaCost = "{2}{U}"
        typeLine = "Enchantment — Room"
        oracleText = "At the beginning of your upkeep, surveil 1. You win the game if there are " +
            "eight or more different names among unlocked doors of Rooms you control."

        triggeredAbility {
            trigger = Triggers.YourUpkeep
            effect = Effects.Surveil(1)
            description = "At the beginning of your upkeep, surveil 1."
        }

        stateTriggeredAbility {
            condition = Compare(
                DynamicAmount.UnlockedDoors(Player.You, distinctNames = true),
                ComparisonOperator.GTE,
                DynamicAmount.Fixed(8),
            )
            effect = Effects.WinGame(message = "Eight doors stood open — the stairs led out.")
            description = "You win the game if there are eight or more different names among " +
                "unlocked doors of Rooms you control."
        }
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "44"
        artist = "Cristi Balanescu"
        imageUri = "https://cards.scryfall.io/normal/front/e/5/e548befc-4cd4-46be-951f-045452261cda.jpg?1726867824"
        ruling("2024-09-20", "Two or more objects have the same name if they have at least one name in common, even if one or more of those objects have additional names. Central Elevator's ability lets you search your library for any Room card that doesn't share a name with a Room you control on the battlefield, checking only the names of unlocked doors. If a Room on the battlefield has no unlocked doors, it doesn't have either of its names.")
        ruling("2024-09-20", "Promising Stairs's ability counts names, not permanents. A Room with two unlocked doors has two names.")
    }
}
