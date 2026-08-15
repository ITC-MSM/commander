package com.wingedsheep.mtg.sets.definitions.dsk.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.CardLayout
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.ManaRestriction

/**
 * Smoky Lounge // Misty Salon (DSK 235) — split-layout Room (CR 709.5).
 *
 * Smoky Lounge {2}{R} — Enchantment — Room
 *   At the beginning of your first main phase, add {R}{R}. Spend this mana only to cast Room spells
 *   and unlock doors.
 *
 * Misty Salon {3}{U} — Enchantment — Room
 *   When you unlock this door, create an X/X blue Spirit creature token with flying, where X is the
 *   number of unlocked doors among Rooms you control.
 *
 * Cast each half separately; the cast face enters unlocked, the other locked. Pay the locked
 * face's printed mana cost as a sorcery-speed special action to unlock it (CR 709.5e). Each face's
 * ability only functions while that door is unlocked.
 *
 * Smoky Lounge's restricted ritual is the existing [Effects.AddMana] with a
 * [ManaRestriction.AnyOf] over "cast a Room spell" ([ManaRestriction.SubtypeSpellsOnly]) and
 * "unlock a door" ([ManaRestriction.UnlockDoorOnly]) — the two ways the printed text lets you
 * spend it. Misty Salon's token uses [Effects.CreateDynamicToken] with
 * [DynamicAmounts.unlockedDoors] feeding both power and toughness; X is recomputed at resolution,
 * so the Misty Salon door that triggered (now unlocked) is itself counted (CR 709.5).
 */
val SmokyLoungeMistySalon = card("Smoky Lounge // Misty Salon") {
    layout = CardLayout.SPLIT
    colorIdentity = "UR"

    face("Smoky Lounge") {
        manaCost = "{2}{R}"
        typeLine = "Enchantment — Room"
        oracleText = "At the beginning of your first main phase, add {R}{R}. Spend this mana only " +
            "to cast Room spells and unlock doors."

        triggeredAbility {
            trigger = Triggers.FirstMainPhase
            effect = Effects.AddMana(
                color = Color.RED,
                amount = 2,
                restriction = ManaRestriction.AnyOf(
                    listOf(
                        ManaRestriction.SubtypeSpellsOnly(setOf("Room")),
                        ManaRestriction.UnlockDoorOnly,
                    )
                ),
            )
            description = "At the beginning of your first main phase, add {R}{R}. Spend this mana " +
                "only to cast Room spells and unlock doors."
        }
    }

    face("Misty Salon") {
        manaCost = "{3}{U}"
        typeLine = "Enchantment — Room"
        oracleText = "When you unlock this door, create an X/X blue Spirit creature token with " +
            "flying, where X is the number of unlocked doors among Rooms you control."

        triggeredAbility {
            trigger = Triggers.OnDoorUnlocked
            effect = Effects.CreateDynamicToken(
                dynamicPower = DynamicAmounts.unlockedDoors(),
                dynamicToughness = DynamicAmounts.unlockedDoors(),
                colors = setOf(Color.BLUE),
                creatureTypes = setOf("Spirit"),
                keywords = setOf(Keyword.FLYING),
                imageUri = "https://cards.scryfall.io/normal/front/9/0/90f6d606-55ca-4431-ae61-5d4f05259403.jpg?1726236595",
            )
            description = "When you unlock this door, create an X/X blue Spirit creature token " +
                "with flying, where X is the number of unlocked doors among Rooms you control."
        }
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "235"
        artist = "Marco Gorlei"
        imageUri = "https://cards.scryfall.io/normal/front/4/7/4700987d-fc55-44eb-bc9f-0e0316ca65e2.jpg?1726780766"
    }
}
