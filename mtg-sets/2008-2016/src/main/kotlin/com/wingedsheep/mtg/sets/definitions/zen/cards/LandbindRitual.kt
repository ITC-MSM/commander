package com.wingedsheep.mtg.sets.definitions.zen.cards

import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Landbind Ritual
 * {3}{W}{W}
 * Sorcery
 * You gain 2 life for each Plains you control.
 *
 * "2 life for each Plains" is the count times two, not two separate gains — one life-gain
 * event, so a single Ajani's Pridemate trigger.
 */
val LandbindRitual = card("Landbind Ritual") {
    manaCost = "{3}{W}{W}"
    colorIdentity = "W"
    typeLine = "Sorcery"
    oracleText = "You gain 2 life for each Plains you control."

    spell {
        effect = Effects.GainLife(
            DynamicAmount.Multiply(
                DynamicAmounts.battlefield(Player.You, GameObjectFilter.Land.withSubtype("Plains")).count(),
                2,
            )
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "24"
        artist = "Steve Prescott"
        flavorText = "\"Honor this place, for our children's children will stand here and speak these same words again.\"\n—Ayli, Kamsa cleric"
        imageUri = "https://cards.scryfall.io/normal/front/2/4/245357c6-b4cd-40f3-b7c2-413eee767239.jpg"
    }
}
