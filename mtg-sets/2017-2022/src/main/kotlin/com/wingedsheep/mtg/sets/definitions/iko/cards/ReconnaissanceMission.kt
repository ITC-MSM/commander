package com.wingedsheep.mtg.sets.definitions.iko.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantTriggeredAbility
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.TriggeredAbility
import com.wingedsheep.sdk.scripting.effects.MayEffect
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Reconnaissance Mission
 * {2}{U}{U}
 * Enchantment
 * Whenever a creature you control deals combat damage to a player, you may draw a card.
 * Cycling {2} ({2}, Discard this card: Draw a card.)
 *
 * "Whenever a creature you control deals combat damage to a player" is not a trigger on the
 * enchantment — it is an ability the enchantment *grants* to every creature you control, so it
 * fires once per creature that connects. [GrantTriggeredAbility] with a group filter is that
 * shape; the granted ability keeps the SELF binding of
 * [Triggers.DealsCombatDamageToPlayer], which resolves against whichever creature it is riding on.
 *
 * The printed "you may" is a [MayEffect] consent gate around the draw, not an `optional` flag.
 */
val ReconnaissanceMission = card("Reconnaissance Mission") {
    manaCost = "{2}{U}{U}"
    colorIdentity = "U"
    typeLine = "Enchantment"
    oracleText = "Whenever a creature you control deals combat damage to a player, you may draw a card.\nCycling {2} ({2}, Discard this card: Draw a card.)"

    keywordAbility(KeywordAbility.cycling("{2}"))

    staticAbility {
        ability = GrantTriggeredAbility(
            ability = TriggeredAbility.create(
                trigger = Triggers.DealsCombatDamageToPlayer.event,
                binding = Triggers.DealsCombatDamageToPlayer.binding,
                effect = MayEffect(effect = Effects.DrawCards(1))
            ),
            filter = GroupFilter(GameObjectFilter.Creature.youControl())
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "65"
        artist = "Johannes Voss"
        flavorText = "\"The bonders work with creatures that fly, slink, and burrow. Assume they know everything.\"\n—Jirina Kudro"
        imageUri = "https://cards.scryfall.io/normal/front/a/e/ae9f7efa-d125-4f83-825e-172ea099a62a.jpg"
    }
}
