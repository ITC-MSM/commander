package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Nath of the Gilt-Leaf
 * {3}{B}{G}
 * Legendary Creature — Elf Warrior
 * 4/4
 * At the beginning of your upkeep, you may have target opponent discard a card at random.
 * Whenever an opponent discards a card, you may create a 1/1 green Elf Warrior creature token.
 *
 * The upkeep trigger is a targeted optional trigger: `optional = true` on a trigger with a player
 * target — with a single opponent the target is auto-chosen and the "may" is asked when the trigger
 * resolves. The random discard is [Patterns.Hand.discardRandom] aimed at the chosen opponent —
 * Hypnotic Specter's shape.
 *
 * The token trigger is [Triggers.AnyOpponentDiscards], which fires once per discarded card, so
 * an opponent discarding three cards offers three tokens. It fires off Nath's own upkeep discard
 * too, which is the card's point.
 */
val NathOfTheGiltLeaf = card("Nath of the Gilt-Leaf") {
    manaCost = "{3}{B}{G}"
    colorIdentity = "BG"
    typeLine = "Legendary Creature — Elf Warrior"
    power = 4
    toughness = 4
    oracleText = "At the beginning of your upkeep, you may have target opponent discard a card at " +
        "random.\n" +
        "Whenever an opponent discards a card, you may create a 1/1 green Elf Warrior creature token."

    triggeredAbility {
        trigger = Triggers.YourUpkeep
        optional = true
        val opponent = target("target opponent", Targets.Opponent)
        effect = Patterns.Hand.discardRandom(1, opponent)
        description = "you may have target opponent discard a card at random."
    }

    triggeredAbility {
        trigger = Triggers.AnyOpponentDiscards
        optional = true
        effect = Effects.CreateToken(
            power = 1,
            toughness = 1,
            colors = setOf(Color.GREEN),
            creatureTypes = setOf("Elf", "Warrior"),
            imageUri = "https://cards.scryfall.io/normal/front/2/7/27b171ac-b2ef-4a80-92d1-6d9e71f3e3ca.jpg?1783942838",
        )
        description = "you may create a 1/1 green Elf Warrior creature token."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "250"
        artist = "Kev Walker"
        flavorText = "A savage hunter with a prince's bearing."
        imageUri = "https://cards.scryfall.io/normal/front/0/c/0c6c69bd-a8bf-4085-85fa-364b8e92b88a.jpg?1783942852"
    }
}
