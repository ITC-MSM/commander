package com.wingedsheep.mtg.sets.definitions.zen.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Goblin Shortcutter
 * {1}{R}
 * Creature — Goblin Scout
 * 2/1
 * When this creature enters, target creature can't block this turn.
 */
val GoblinShortcutter = card("Goblin Shortcutter") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Goblin Scout"
    power = 2
    toughness = 1
    oracleText = "When this creature enters, target creature can't block this turn."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val creature = target("creature", Targets.Creature)
        effect = Effects.CantBlock(creature)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "128"
        artist = "Jesper Ejsing"
        flavorText = "\"Goblins are cheap, but be careful. It's a lot easier to steal from a corpse than a customer.\"\n—Samila, Murasa Expeditionary House"
        imageUri = "https://cards.scryfall.io/normal/front/5/d/5daeaa2e-68e5-4f49-9220-58c0c9b1a3d0.jpg"
    }
}
