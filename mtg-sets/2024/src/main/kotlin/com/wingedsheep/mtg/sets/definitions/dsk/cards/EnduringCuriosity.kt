package com.wingedsheep.mtg.sets.definitions.dsk.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.enduring
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.events.DamageType
import com.wingedsheep.sdk.scripting.events.RecipientFilter

/**
 * Enduring Curiosity
 * {2}{U}{U}
 * Enchantment Creature — Cat Glimmer
 * 4/3
 * Flash
 * Whenever a creature you control deals combat damage to a player, draw a card.
 * When Enduring Curiosity dies, if it was a creature, return it to the battlefield under its
 *   owner's control. It's an enchantment. (It's not a creature.)
 *
 * The combat-damage trigger is the generic "a creature you control deals combat damage to a
 * player" shape (binding ANY — any creature you control can be the source). The death clause is
 * the Duskmourn "Enduring" mechanic — see [enduring].
 */
val EnduringCuriosity = card("Enduring Curiosity") {
    manaCost = "{2}{U}{U}"
    colorIdentity = "U"
    typeLine = "Enchantment Creature — Cat Glimmer"
    oracleText = "Flash\n" +
        "Whenever a creature you control deals combat damage to a player, draw a card.\n" +
        "When Enduring Curiosity dies, if it was a creature, return it to the battlefield under " +
        "its owner's control. It's an enchantment. (It's not a creature.)"
    power = 4
    toughness = 3

    keywords(Keyword.FLASH)
    enduring()

    triggeredAbility {
        trigger = Triggers.dealsDamage(
            damageType = DamageType.Combat,
            recipient = RecipientFilter.AnyPlayer,
            sourceFilter = GameObjectFilter.Creature.youControl(),
            binding = TriggerBinding.ANY
        )
        effect = Effects.DrawCards(1)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "51"
        artist = "Julie Dillon"
        imageUri = "https://cards.scryfall.io/normal/front/8/6/8616629e-08f9-41ad-bfec-f86c8096f1cb.jpg?1726286044"
    }
}
