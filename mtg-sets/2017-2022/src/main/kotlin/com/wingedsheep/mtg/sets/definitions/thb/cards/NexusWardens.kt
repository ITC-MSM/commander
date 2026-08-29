package com.wingedsheep.mtg.sets.definitions.thb.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding

/**
 * Nexus Wardens
 * {2}{G}
 * Creature — Satyr Archer
 * 1/4
 *
 * Reach
 * Constellation — Whenever an enchantment you control enters, you gain 2 life.
 *
 * Constellation is an ability word with no rules meaning of its own, so this is a plain
 * enters-the-battlefield watcher over enchantments you control with [TriggerBinding.ANY] — the
 * Wardens themselves are not an enchantment, so the trigger must not be bound to the source.
 */
val NexusWardens = card("Nexus Wardens") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Satyr Archer"
    power = 1
    toughness = 4
    oracleText = "Reach\n" +
        "Constellation — Whenever an enchantment you control enters, you gain 2 life."

    keywords(Keyword.REACH)

    triggeredAbility {
        trigger = Triggers.entersBattlefield(
            filter = GameObjectFilter.Enchantment.youControl(),
            binding = TriggerBinding.ANY,
        )
        effect = Effects.GainLife(2)
        description = "Constellation — Whenever an enchantment you control enters, you gain 2 life."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "184"
        artist = "Dan Murayama Scott"
        flavorText = "The Summer Nexus is a holy grove in Setessa where the starfields of Nyx glitter in the shadows."
        imageUri = "https://cards.scryfall.io/normal/front/6/8/68cb4e56-bcad-43a3-8600-a3594047205a.jpg"
    }
}
