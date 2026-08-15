package com.wingedsheep.mtg.sets.definitions.dsk.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.CardLayout
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Restricted Office // Lecture Hall (DSK 227) — split-layout Room (CR 709.5).
 *
 * Restricted Office {2}{W}{W} — Enchantment — Room
 *   When you unlock this door, destroy all creatures with power 3 or greater.
 *
 * Lecture Hall {5}{U}{U} — Enchantment — Room
 *   Other permanents you control have hexproof.
 *
 * Cast each half separately; the cast face enters unlocked, the other locked. Pay the locked
 * face's printed mana cost as a sorcery-speed special action to unlock it (CR 709.5e). The
 * Lecture Hall lord uses a battlefield-scoped [GrantKeyword] excluding the source itself, so the
 * Room doesn't give itself hexproof.
 */
val RestrictedOfficeLectureHall = card("Restricted Office // Lecture Hall") {
    layout = CardLayout.SPLIT
    colorIdentity = "WU"

    face("Restricted Office") {
        manaCost = "{2}{W}{W}"
        typeLine = "Enchantment — Room"
        oracleText = "When you unlock this door, destroy all creatures with power 3 or greater."

        triggeredAbility {
            trigger = Triggers.OnDoorUnlocked
            effect = Effects.DestroyAll(GameObjectFilter.Creature.powerAtLeast(3))
            description = "When you unlock this door, destroy all creatures with power 3 or greater."
        }
    }

    face("Lecture Hall") {
        manaCost = "{5}{U}{U}"
        typeLine = "Enchantment — Room"
        oracleText = "Other permanents you control have hexproof."

        staticAbility {
            ability = GrantKeyword(
                Keyword.HEXPROOF,
                GroupFilter.AllPermanentsYouControl.other()
            )
        }
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "227"
        artist = "Antonio José Manzanedo"
        imageUri = "https://cards.scryfall.io/normal/front/a/c/ac794b2f-b5ea-449d-bb0b-4f0e6cc145ef.jpg?1726867717"
    }
}
