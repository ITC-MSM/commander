package com.wingedsheep.mtg.sets.definitions.grn.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.MayPayManaEffect

/**
 * Dawn of Hope
 * {1}{W}
 * Enchantment
 * Whenever you gain life, you may pay {2}. If you do, draw a card.
 * {3}{W}: Create a 1/1 white Soldier creature token with lifelink.
 */
val DawnOfHope = card("Dawn of Hope") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Enchantment"
    oracleText = "Whenever you gain life, you may pay {2}. If you do, draw a card.\n" +
        "{3}{W}: Create a 1/1 white Soldier creature token with lifelink."

    triggeredAbility {
        trigger = Triggers.YouGainLife
        effect = MayPayManaEffect(ManaCost.parse("{2}"), Effects.DrawCards(1))
    }
    activatedAbility {
        cost = Costs.Mana("{3}{W}")
        effect = Effects.CreateToken(
            power = 1,
            toughness = 1,
            colors = setOf(Color.WHITE),
            creatureTypes = setOf("Soldier"),
            keywords = setOf(Keyword.LIFELINK)
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "8"
        artist = "Sung Choi"
        flavorText = "\"To wage war, secure peace within yourself.\"\n—Emmara"
        imageUri = "https://cards.scryfall.io/normal/front/c/f/cf2a9e82-8670-4b7f-b5f0-8e10f8aeff1c.jpg?1783934203"
    }
}
