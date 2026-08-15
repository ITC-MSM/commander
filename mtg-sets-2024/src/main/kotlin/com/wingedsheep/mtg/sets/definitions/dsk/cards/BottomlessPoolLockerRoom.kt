package com.wingedsheep.mtg.sets.definitions.dsk.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.CardLayout
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EventPattern.OneOrMoreDealCombatDamageToPlayerEvent
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.TriggerSpec

/**
 * Bottomless Pool // Locker Room (DSK 43) — split-layout Room (CR 709.5).
 *
 * Bottomless Pool {U} — Enchantment — Room
 *   When you unlock this door, return up to one target creature to its owner's hand.
 *
 * Locker Room {4}{U} — Enchantment — Room
 *   Whenever one or more creatures you control deal combat damage to a player, draw a card.
 *
 * Cast each half separately; the cast face enters unlocked, the other locked. Pay the locked
 * face's printed mana cost as a sorcery-speed special action to unlock it (CR 709.5e).
 */
val BottomlessPoolLockerRoom = card("Bottomless Pool // Locker Room") {
    layout = CardLayout.SPLIT
    colorIdentity = "U"

    face("Bottomless Pool") {
        manaCost = "{U}"
        typeLine = "Enchantment — Room"
        oracleText = "When you unlock this door, return up to one target creature to its owner's hand."

        triggeredAbility {
            trigger = Triggers.OnDoorUnlocked
            val creature = target("up to one target creature", Targets.UpToCreatures(1))
            effect = Effects.ReturnToHand(creature)
        }
    }

    face("Locker Room") {
        manaCost = "{4}{U}"
        typeLine = "Enchantment — Room"
        oracleText = "Whenever one or more creatures you control deal combat damage to a player, draw a card."

        triggeredAbility {
            trigger = TriggerSpec(
                OneOrMoreDealCombatDamageToPlayerEvent(
                    sourceFilter = GameObjectFilter.Creature.youControl()
                ),
                TriggerBinding.ANY
            )
            effect = Effects.DrawCards(1)
            description = "Whenever one or more creatures you control deal combat damage to a player, draw a card."
        }
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "43"
        artist = "Diana Franco"
        imageUri = "https://cards.scryfall.io/normal/front/6/0/606fe87c-d17b-4fa7-8e82-e7002d8229ef.jpg?1726780385"
    }
}
