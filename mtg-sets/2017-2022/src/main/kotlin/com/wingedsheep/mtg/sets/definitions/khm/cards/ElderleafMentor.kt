package com.wingedsheep.mtg.sets.definitions.khm.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Elderleaf Mentor
 * {3}{G}
 * Creature — Elf Warrior
 * 3/2
 * When this creature enters, create a 1/1 green Elf Warrior creature token.
 *
 * Four mana for two Elf Warrior bodies — the common that feeds Kaldheim's Elf count.
 */
val ElderleafMentor = card("Elderleaf Mentor") {
    manaCost = "{3}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Elf Warrior"
    oracleText = "When this creature enters, create a 1/1 green Elf Warrior creature token."
    power = 3
    toughness = 2

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.CreateToken(
            power = 1,
            toughness = 1,
            colors = setOf(Color.GREEN),
            creatureTypes = setOf("Elf", "Warrior")
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "165"
        artist = "Zoltan Boros"
        flavorText = "\"Our ancestors grew complacent in their divinity, allowing their lessers to topple them. Never again.\""
        imageUri = "https://cards.scryfall.io/normal/front/5/e/5e8bded3-46c3-474f-9d09-978df8705ad1.jpg"
    }
}
