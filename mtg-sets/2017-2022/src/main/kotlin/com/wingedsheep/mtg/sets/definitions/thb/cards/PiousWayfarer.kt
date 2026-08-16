package com.wingedsheep.mtg.sets.definitions.thb.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding

/**
 * Pious Wayfarer
 * {W}
 * Creature — Human Scout
 * 1/2
 * Constellation — Whenever an enchantment you control enters, target creature gets +1/+1 until end of turn.
 *
 * Constellation is an ability word — no rules meaning of its own. The trigger is a plain
 * enters-the-battlefield watcher over enchantments you control, which is why an enchantment
 * *creature* entering both triggers it and is a legal target for it.
 */
val PiousWayfarer = card("Pious Wayfarer") {
    manaCost = "{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Scout"
    power = 1
    toughness = 2
    oracleText = "Constellation — Whenever an enchantment you control enters, target creature gets +1/+1 until end of turn."

    triggeredAbility {
        trigger = Triggers.entersBattlefield(
            filter = GameObjectFilter.Enchantment.youControl(),
            binding = TriggerBinding.ANY,
        )
        val t = target("target", Targets.Creature)
        effect = Effects.ModifyStats(1, 1, t)
        description = "Constellation — Whenever an enchantment you control enters, target creature gets +1/+1 until end of turn."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "32"
        artist = "Mark Zug"
        flavorText = "Every footstep is a prayer, every road a temple."
        imageUri = "https://cards.scryfall.io/normal/front/1/0/10b1a2b5-5be2-43a2-bc35-95df6eb0984f.jpg?1783931591"

        ruling(
            "2020-01-24",
            "If an enchantment creature enters the battlefield under your control, Pious Wayfarer's " +
                "ability can target it."
        )
        ruling(
            "2020-01-24",
            "A constellation ability triggers whenever an enchantment enters the battlefield under " +
                "your control for any reason. Enchantments with other card types, such as enchantment " +
                "creatures, will also cause constellation abilities to trigger."
        )
        ruling(
            "2020-01-24",
            "An Aura spell that has an illegal target when it tries to resolve doesn't resolve and is " +
                "instead put into its owner's graveyard. It doesn't enter the battlefield, so " +
                "constellation abilities don't trigger."
        )
    }
}
