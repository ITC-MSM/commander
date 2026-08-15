package com.wingedsheep.mtg.sets.definitions.inv.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Quirion Sentinel
 * {1}{G}
 * Creature — Elf Druid
 * 2/1
 * When this creature enters, add one mana of any color.
 *
 * The mana boost is a triggered ability (uses the stack), not a mana ability, so it
 * resolves like any other ETB trigger and the mana lands in the controller's pool.
 */
val QuirionSentinel = card("Quirion Sentinel") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Elf Druid"
    power = 2
    toughness = 1
    oracleText = "When this creature enters, add one mana of any color."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.AddAnyColorMana()
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "204"
        artist = "Heather Hudson"
        flavorText = "All elvenkind stood against Phyrexia. The Quirion nation deployed its most spiritual adepts, who wielded the power of their native soil."
        imageUri = "https://cards.scryfall.io/normal/front/2/f/2fc639ea-a925-4f1e-879f-b8fcb12bf257.jpg?1562904752"
    }
}
