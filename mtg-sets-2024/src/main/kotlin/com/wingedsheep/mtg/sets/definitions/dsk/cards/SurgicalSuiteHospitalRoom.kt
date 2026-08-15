package com.wingedsheep.mtg.sets.definitions.dsk.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.CardLayout
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Surgical Suite // Hospital Room (DSK 34) — split-layout Room (CR 709.5).
 *
 * Surgical Suite {1}{W} — Enchantment — Room
 *   When you unlock this door, return target creature card with mana value 3 or less from your
 *   graveyard to the battlefield.
 *
 * Hospital Room {3}{W} — Enchantment — Room
 *   Whenever you attack, put a +1/+1 counter on target attacking creature.
 *
 * Cast each half separately; the cast face enters unlocked, the other locked. Pay the locked
 * face's printed mana cost as a sorcery-speed special action to unlock it (CR 709.5e).
 */
val SurgicalSuiteHospitalRoom = card("Surgical Suite // Hospital Room") {
    layout = CardLayout.SPLIT
    colorIdentity = "W"

    face("Surgical Suite") {
        manaCost = "{1}{W}"
        typeLine = "Enchantment — Room"
        oracleText = "When you unlock this door, return target creature card with mana value 3 or " +
            "less from your graveyard to the battlefield."

        triggeredAbility {
            trigger = Triggers.OnDoorUnlocked
            val t = target(
                "target creature card with mana value 3 or less",
                TargetObject(filter = TargetFilter.CreatureInYourGraveyard.manaValueAtMost(3))
            )
            effect = Effects.Move(t, Zone.BATTLEFIELD, fromZone = Zone.GRAVEYARD)
            description = "When you unlock this door, return target creature card with mana value " +
                "3 or less from your graveyard to the battlefield."
        }
    }

    face("Hospital Room") {
        manaCost = "{3}{W}"
        typeLine = "Enchantment — Room"
        oracleText = "Whenever you attack, put a +1/+1 counter on target attacking creature."

        triggeredAbility {
            trigger = Triggers.YouAttack
            val attacker = target("target attacking creature", Targets.AttackingCreature)
            effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, attacker)
            description = "Whenever you attack, put a +1/+1 counter on target attacking creature."
        }
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "34"
        artist = "Titus Lunter"
        imageUri = "https://cards.scryfall.io/normal/front/9/6/96b2dc14-4477-4444-a9eb-4fa4c02dfbde.jpg?1726780782"
    }
}
