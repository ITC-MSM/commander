package com.wingedsheep.mtg.sets.definitions.ths.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.conditions.Exists
import com.wingedsheep.sdk.scripting.references.Player

/**
 * Lagonna-Band Elder
 * {2}{W}
 * Creature — Centaur Advisor
 * 3 / 2
 *
 * When this creature enters, if you control an enchantment, you gain 3 life.
 */
val LagonnaBandElder = card("Lagonna-Band Elder") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Centaur Advisor"
    power = 3
    toughness = 2
    oracleText = "When this creature enters, if you control an enchantment, you gain 3 life."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        interveningIf = Exists(Player.You, Zone.BATTLEFIELD, GameObjectFilter.Enchantment)
        effect = Effects.GainLife(3)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "21"
        artist = "Min Yum"
        flavorText = "\"The best lessons are not the ones I teach. They are the ones the pupils realize for themselves.\""
        imageUri = "https://cards.scryfall.io/normal/front/6/8/68b9df54-e78e-4a86-a8b1-b735ec08a812.jpg"
    }
}
