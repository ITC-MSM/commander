package com.wingedsheep.mtg.sets.definitions.bng.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CanOnlyBlockCreaturesWith
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Stratus Walk
 * {1}{U}
 * Enchantment — Aura
 *
 * Enchant creature
 * When this Aura enters, draw a card.
 * Enchanted creature has flying. (It can't be blocked except by creatures with flying or reach.)
 * Enchanted creature can block only creatures with flying.
 *
 * The block restriction is a separate static from the flying grant — it constrains what the host
 * may block rather than granting it anything, so the two cannot fold into one ability. Its `filter`
 * must be spelled [GroupFilter.attachedCreature]: `CanOnlyBlockCreaturesWith` defaults to
 * `GroupFilter.source()`, which on an Aura is the Aura itself — an enchantment that never blocks,
 * so the restriction would silently do nothing and the enchanted creature would keep blocking
 * anything.
 */
val StratusWalk = card("Stratus Walk") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Enchantment — Aura"
    oracleText = "Enchant creature\n" +
        "When this Aura enters, draw a card.\n" +
        "Enchanted creature has flying. (It can't be blocked except by creatures with flying or reach.)\n" +
        "Enchanted creature can block only creatures with flying."

    auraTarget = Targets.Creature

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.DrawCards(1)
    }

    staticAbility {
        ability = GrantKeyword(Keyword.FLYING)
    }

    staticAbility {
        ability = CanOnlyBlockCreaturesWith(
            blockerFilter = GameObjectFilter.Creature.withKeyword(Keyword.FLYING),
            filter = GroupFilter.attachedCreature()
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "52"
        artist = "Aaron Miller"
        imageUri = "https://cards.scryfall.io/normal/front/4/4/44744725-6ba7-40bd-b25b-788a1032ecf4.jpg"
    }
}
