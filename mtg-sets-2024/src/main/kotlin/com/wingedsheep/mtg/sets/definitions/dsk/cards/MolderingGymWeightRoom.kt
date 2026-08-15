package com.wingedsheep.mtg.sets.definitions.dsk.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.CardLayout
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.effects.GatherCardsEffect
import com.wingedsheep.sdk.scripting.effects.SearchDestination
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Moldering Gym // Weight Room (DSK 190) — split-layout Room (CR 709.5).
 *
 * Moldering Gym {2}{G} — Enchantment — Room
 *   When you unlock this door, search your library for a basic land card, put it onto the
 *   battlefield tapped, then shuffle.
 *
 * Weight Room {5}{G} — Enchantment — Room
 *   When you unlock this door, manifest dread, then put three +1/+1 counters on that creature.
 *
 * Weight Room reuses the shared [Patterns.Library.manifestDread] recipe with `markEntered = true`,
 * then gathers the just-manifested creature via [CardSource.EnteredViaThisResolution] and adds the
 * three counters — the same "manifest dread, then counters on those creatures" shape as
 * Valgavoth's Onslaught. If the library is empty (or the manifested creature has already left the
 * battlefield) the gather is empty and no counters land.
 *
 * Cast each half separately; the cast face enters unlocked, the other locked. Pay the locked
 * face's printed mana cost as a sorcery-speed special action to unlock it (CR 709.5e).
 */
val MolderingGymWeightRoom = card("Moldering Gym // Weight Room") {
    layout = CardLayout.SPLIT
    colorIdentity = "G"

    face("Moldering Gym") {
        manaCost = "{2}{G}"
        typeLine = "Enchantment — Room"
        oracleText = "When you unlock this door, search your library for a basic land card, put it onto the battlefield tapped, then shuffle."

        triggeredAbility {
            trigger = Triggers.OnDoorUnlocked
            effect = Patterns.Library.searchLibrary(
                filter = GameObjectFilter.BasicLand,
                destination = SearchDestination.BATTLEFIELD,
                entersTapped = true
            )
        }
    }

    face("Weight Room") {
        manaCost = "{5}{G}"
        typeLine = "Enchantment — Room"
        oracleText = "When you unlock this door, manifest dread, then put three +1/+1 counters on that creature."

        triggeredAbility {
            trigger = Triggers.OnDoorUnlocked
            effect = Effects.Composite(
                listOf(
                    Patterns.Library.manifestDread(markEntered = true),
                    GatherCardsEffect(
                        source = CardSource.EnteredViaThisResolution,
                        storeAs = "weightRoomManifested"
                    ),
                    Effects.AddCountersToCollection(
                        collectionName = "weightRoomManifested",
                        counterType = Counters.PLUS_ONE_PLUS_ONE,
                        amount = DynamicAmount.Fixed(3)
                    )
                )
            )
        }
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "190"
        artist = "Helge C. Balzer"
        imageUri = "https://cards.scryfall.io/normal/front/2/4/245d5a61-c40c-4039-aea8-3ad61415b8f0.jpg?1726780702"
    }
}
