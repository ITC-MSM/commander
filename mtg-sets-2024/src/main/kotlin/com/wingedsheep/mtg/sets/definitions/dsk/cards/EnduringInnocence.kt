package com.wingedsheep.mtg.sets.definitions.dsk.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.enduring
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding

/**
 * Enduring Innocence
 * {1}{W}{W}
 * Enchantment Creature — Sheep Glimmer
 * 2/1
 * Lifelink
 * Whenever one or more other creatures you control with power 2 or less enter, draw a card.
 *   This ability triggers only once each turn.
 * When Enduring Innocence dies, if it was a creature, return it to the battlefield under its
 *   owner's control. It's an enchantment. (It's not a creature.)
 *
 * "One or more ... This ability triggers only once each turn" — modeled as a per-creature
 * "another creature you control with power 2 or less enters" trigger gated `oncePerTurn`. The
 * once-per-turn cap makes the per-creature shape behaviorally identical to the printed batch:
 * the first matching enter each turn draws, and any further (including simultaneous) matches are
 * suppressed for the rest of the turn.
 *
 * The death clause is the Duskmourn "Enduring" mechanic — see [enduring].
 */
val EnduringInnocence = card("Enduring Innocence") {
    manaCost = "{1}{W}{W}"
    colorIdentity = "W"
    typeLine = "Enchantment Creature — Sheep Glimmer"
    oracleText = "Lifelink\n" +
        "Whenever one or more other creatures you control with power 2 or less enter, draw a card. " +
        "This ability triggers only once each turn.\n" +
        "When Enduring Innocence dies, if it was a creature, return it to the battlefield under " +
        "its owner's control. It's an enchantment. (It's not a creature.)"
    power = 2
    toughness = 1

    keywords(Keyword.LIFELINK)
    enduring()

    triggeredAbility {
        trigger = Triggers.entersBattlefield(
            filter = GameObjectFilter.Creature.powerAtMost(2).youControl(),
            binding = TriggerBinding.OTHER
        )
        oncePerTurn = true
        effect = Effects.DrawCards(1)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "6"
        artist = "Liiga Smilshkalne"
        imageUri = "https://cards.scryfall.io/normal/front/0/8/08f79439-b8f8-418f-9772-26d81844749e.jpg?1726285879"
    }
}
