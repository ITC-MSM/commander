package com.wingedsheep.mtg.sets.definitions.ths.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ModifyStats

/**
 * Scourgemark
 * {1}{B}
 * Enchantment — Aura
 *
 * Enchant creature
 * When this Aura enters, draw a card.
 * Enchanted creature gets +1/+0.
 */
val Scourgemark = card("Scourgemark") {
    manaCost = "{1}{B}"
    colorIdentity = "B"
    typeLine = "Enchantment — Aura"
    oracleText = "Enchant creature\nWhen this Aura enters, draw a card.\nEnchanted creature gets +1/+0."

    auraTarget = Targets.Creature

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.DrawCards(1)
    }

    staticAbility {
        ability = ModifyStats(1, 0)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "105"
        artist = "Franz Vohwinkel"
        flavorText = "To members of the cult of Erebos, gold-infused tattoos symbolize the inevitable grasp of the god of death."
        imageUri = "https://cards.scryfall.io/normal/front/e/d/ed36a150-a686-4fa6-8364-8be262ad7d98.jpg"
    }
}
