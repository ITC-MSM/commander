package com.wingedsheep.mtg.sets.definitions.dsk.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.CardLayout
import com.wingedsheep.sdk.model.Rarity

/**
 * Meat Locker // Drowned Diner (DSK 65) — split-layout Room (CR 709.5).
 *
 * Meat Locker {2}{U} — Enchantment — Room
 *   When you unlock this door, tap up to one target creature and put two stun counters on it.
 *
 * Drowned Diner {3}{U}{U} — Enchantment — Room
 *   When you unlock this door, draw three cards, then discard a card.
 *
 * Cast each half separately; the cast face enters unlocked, the other locked. Pay the locked
 * face's printed mana cost as a sorcery-speed special action to unlock it (CR 709.5e).
 */
val MeatLockerDrownedDiner = card("Meat Locker // Drowned Diner") {
    layout = CardLayout.SPLIT
    colorIdentity = "U"

    face("Meat Locker") {
        manaCost = "{2}{U}"
        typeLine = "Enchantment — Room"
        oracleText = "When you unlock this door, tap up to one target creature and put two stun counters on it."

        triggeredAbility {
            trigger = Triggers.OnDoorUnlocked
            val t = target("up to one target creature", Targets.UpToCreatures(1))
            effect = Effects.Tap(t).then(Effects.AddCounters("STUN", 2, t))
            description = "When you unlock this door, tap up to one target creature and put two stun counters on it."
        }
    }

    face("Drowned Diner") {
        manaCost = "{3}{U}{U}"
        typeLine = "Enchantment — Room"
        oracleText = "When you unlock this door, draw three cards, then discard a card."

        triggeredAbility {
            trigger = Triggers.OnDoorUnlocked
            effect = Patterns.Hand.loot(draw = 3, discard = 1)
            description = "When you unlock this door, draw three cards, then discard a card."
        }
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "65"
        artist = "Sergey Glushakov"
        imageUri = "https://cards.scryfall.io/normal/front/b/3/b3c773f0-9e65-48de-a362-a9a943198693.jpg?1726780679"
    }
}
