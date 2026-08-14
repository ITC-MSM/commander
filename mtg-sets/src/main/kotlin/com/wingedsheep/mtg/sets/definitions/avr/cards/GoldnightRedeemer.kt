package com.wingedsheep.mtg.sets.definitions.avr.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Goldnight Redeemer
 * {4}{W}{W}
 * Creature — Angel
 * 4/4
 * Flying
 * When this creature enters, you gain 2 life for each other creature you control.
 */
val GoldnightRedeemer = card("Goldnight Redeemer") {
    manaCost = "{4}{W}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Angel"
    oracleText =
        "Flying\nWhen this creature enters, you gain 2 life for each other creature you control."
    power = 4
    toughness = 4

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.GainLife(
            DynamicAmount.Multiply(
                DynamicAmount.AggregateBattlefield(
                    player = Player.You,
                    filter = GameObjectFilter.Creature,
                    excludeSelf = true,
                ),
                2,
            ),
            EffectTarget.Controller,
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "23"
        artist = "Karl Kopinski"
        flavorText =
            "The Host sings of Avacyn's return without a single verse about its own suffering."
        imageUri =
            "https://cards.scryfall.io/normal/front/d/f/df5656e3-5f53-41f8-9f24-04caad5e4ca3.jpg?1783940734"
    }
}
