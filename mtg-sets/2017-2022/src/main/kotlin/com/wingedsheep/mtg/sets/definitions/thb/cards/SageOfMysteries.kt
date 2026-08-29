package com.wingedsheep.mtg.sets.definitions.thb.cards

import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding

/**
 * Sage of Mysteries
 * {U}
 * Creature — Human Wizard
 * 0/2
 *
 * Constellation — Whenever an enchantment you control enters, target player mills two cards.
 *
 * Constellation is an ability word with no rules meaning of its own — a plain enters-the-battlefield
 * watcher over enchantments you control, bound with [TriggerBinding.ANY] so the Sage (not itself an
 * enchantment) isn't the thing being watched. The mill is [Patterns.Library.mill]'s gather-then-move
 * pipeline aimed at the chosen player rather than the controller.
 */
val SageOfMysteries = card("Sage of Mysteries") {
    manaCost = "{U}"
    colorIdentity = "U"
    typeLine = "Creature — Human Wizard"
    power = 0
    toughness = 2
    oracleText = "Constellation — Whenever an enchantment you control enters, target player mills two cards."

    triggeredAbility {
        trigger = Triggers.entersBattlefield(
            filter = GameObjectFilter.Enchantment.youControl(),
            binding = TriggerBinding.ANY,
        )
        val t = target("target", Targets.Player)
        effect = Patterns.Library.mill(2, t)
        description = "Constellation — Whenever an enchantment you control enters, target player mills two cards."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "62"
        artist = "John Stanko"
        flavorText = "\"I see destruction, suffering, and one tormented glimmer of hope.\""
        imageUri = "https://cards.scryfall.io/normal/front/4/1/4138fd65-e0c3-42a1-9c0d-4d5f20228b55.jpg"
    }
}
