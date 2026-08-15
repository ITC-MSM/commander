package com.wingedsheep.mtg.sets.definitions.dsk.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.CardLayout
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.AdditionalSourceTriggers
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Mirror Room // Fractured Realm (DSK 67) — split-layout Room (CR 709.5).
 *
 * Mirror Room {2}{U} — Enchantment — Room
 *   When you unlock this door, create a token that's a copy of target creature you control,
 *   except it's a Reflection in addition to its other creature types.
 *
 * Fractured Realm {5}{U}{U} — Enchantment — Room
 *   If a triggered ability of a permanent you control triggers, that ability triggers an
 *   additional time.
 *
 * Cast each half separately; the cast face enters unlocked, the other locked. Pay the locked
 * face's printed mana cost as a sorcery-speed special action to unlock it (CR 709.5e).
 *
 * The back face says "a permanent you control" — not "another" — so Fractured Realm doubles
 * its own triggered abilities too (excludeSelf = false). It has no triggers of its own, but the
 * intent is faithful to the literal text.
 */
val MirrorRoomFracturedRealm = card("Mirror Room // Fractured Realm") {
    layout = CardLayout.SPLIT
    colorIdentity = "U"

    face("Mirror Room") {
        manaCost = "{2}{U}"
        typeLine = "Enchantment — Room"
        oracleText = "When you unlock this door, create a token that's a copy of target creature " +
            "you control, except it's a Reflection in addition to its other creature types."

        triggeredAbility {
            trigger = Triggers.OnDoorUnlocked
            val creature = target("target creature you control", Targets.CreatureYouControl)
            effect = Effects.CreateTokenCopyOfTarget(
                target = creature,
                addedSubtypes = setOf(Subtype("Reflection"))
            )
        }
    }

    face("Fractured Realm") {
        manaCost = "{5}{U}{U}"
        typeLine = "Enchantment — Room"
        oracleText = "If a triggered ability of a permanent you control triggers, that ability " +
            "triggers an additional time."

        staticAbility {
            ability = AdditionalSourceTriggers(
                sourceFilter = GameObjectFilter.Permanent.youControl(),
                excludeSelf = false
            )
        }
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "67"
        artist = "Helge C. Balzer"
        imageUri = "https://cards.scryfall.io/normal/front/c/2/c2e085dd-a448-4f5a-9cfa-5c2034234e7c.jpg?1726867728"
    }
}
