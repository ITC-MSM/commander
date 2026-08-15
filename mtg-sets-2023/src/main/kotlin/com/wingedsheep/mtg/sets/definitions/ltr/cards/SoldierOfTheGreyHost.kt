package com.wingedsheep.mtg.sets.definitions.ltr.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Soldier of the Grey Host
 * {3}{W}
 * Creature — Spirit Soldier
 * 2/2
 *
 * Flash
 * Flying
 * When this creature enters, target creature gets +2/+0 until end of turn.
 */
val SoldierOfTheGreyHost = card("Soldier of the Grey Host") {
    manaCost = "{3}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Spirit Soldier"
    power = 2
    toughness = 2
    oracleText = "Flash\nFlying\nWhen this creature enters, target creature gets +2/+0 until end of turn."

    keywords(Keyword.FLASH, Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val creature = target("target creature", Targets.Creature)
        effect = Effects.ModifyStats(2, 0, creature)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "32"
        artist = "Chris Cold"
        flavorText = "\"The Dead awaken; for the hour is come for the oathbreakers.\"\n—Malbeth the Seer"
        imageUri = "https://cards.scryfall.io/normal/front/b/7/b79999df-af8c-4724-9631-20eee5a00e49.jpg?1686967938"
    }
}
