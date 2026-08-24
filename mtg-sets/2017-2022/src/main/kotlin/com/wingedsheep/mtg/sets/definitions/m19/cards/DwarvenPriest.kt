package com.wingedsheep.mtg.sets.definitions.m19.cards

import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Dwarven Priest
 * {3}{W}
 * Creature — Dwarf Cleric
 * 2/4
 * When this creature enters, you gain 1 life for each creature you control.
 *
 * The count is taken on resolution and includes Dwarven Priest itself, so
 * [DynamicAmounts.creaturesYouControl] (an `AggregateBattlefield` count over creatures you
 * control) is read against projected state rather than latched at the trigger.
 */
val DwarvenPriest = card("Dwarven Priest") {
    manaCost = "{3}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Dwarf Cleric"
    power = 2
    toughness = 4
    oracleText = "When this creature enters, you gain 1 life for each creature you control."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.GainLife(DynamicAmounts.creaturesYouControl())
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "11"
        artist = "Even Amundsen"
        flavorText = "\"These storied halls are under my protection.\""
        imageUri = "https://cards.scryfall.io/normal/front/7/e/7e1e0f13-35a6-4a5e-8666-47bc5c275be7.jpg"
    }
}
