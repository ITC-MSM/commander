package com.wingedsheep.mtg.sets.definitions.thb.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding

/**
 * Captivating Unicorn
 * {4}{W}
 * Creature — Unicorn
 * 4/4
 * Constellation — Whenever an enchantment you control enters, tap target creature an opponent controls.
 *
 * Constellation is an ability word (no rules meaning of its own): the trigger is
 * `entersBattlefield(Enchantment.youControl(), ANY)` — the same atom Eerie uses — into
 * [Effects.Tap] on a [Targets.CreatureOpponentControls]. ANY rather than OTHER because an
 * enchantment *creature* entering is itself an enchantment you control, and the Unicorn is not
 * an enchantment so it can never be the entering permanent anyway.
 */
val CaptivatingUnicorn = card("Captivating Unicorn") {
    manaCost = "{4}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Unicorn"
    power = 4
    toughness = 4
    oracleText = "Constellation — Whenever an enchantment you control enters, tap target creature an opponent controls."

    triggeredAbility {
        trigger = Triggers.entersBattlefield(
            filter = GameObjectFilter.Enchantment.youControl(),
            binding = TriggerBinding.ANY,
        )
        val creature = target("creature", Targets.CreatureOpponentControls)
        effect = Effects.Tap(creature)
        description = "Constellation — Whenever an enchantment you control enters, tap target creature an opponent controls."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "6"
        artist = "Emrah Elmasli"
        flavorText = "\"Gazing at the unicorn, I felt closer to the majesty of Nyx than I ever had before.\" —Oineus, traveling merchant"
        imageUri = "https://cards.scryfall.io/normal/front/1/3/13893599-0b87-4fc0-863d-f3e0ae51cc31.jpg?1783931602"
        ruling("2020-01-24", "A constellation ability triggers whenever an enchantment enters the battlefield under your control for any reason. Enchantments with other card types, such as enchantment creatures, will also cause constellation abilities to trigger.")
        ruling("2020-01-24", "An Aura spell that has an illegal target when it tries to resolve doesn't resolve and is instead put into its owner's graveyard. It doesn't enter the battlefield, so constellation abilities don't trigger.")
    }
}
