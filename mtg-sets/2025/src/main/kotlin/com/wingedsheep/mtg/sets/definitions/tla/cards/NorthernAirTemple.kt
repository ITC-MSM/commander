package com.wingedsheep.mtg.sets.definitions.tla.cards

import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Northern Air Temple
 * {B}
 * Legendary Enchantment — Shrine
 * When Northern Air Temple enters, each opponent loses X life and you gain X life,
 * where X is the number of Shrines you control.
 * Whenever another Shrine you control enters, each opponent loses 1 life and you gain 1 life.
 */
val NorthernAirTemple = card("Northern Air Temple") {
    manaCost = "{B}"
    colorIdentity = "B"
    typeLine = "Legendary Enchantment — Shrine"
    oracleText = "When Northern Air Temple enters, each opponent loses X life and you gain X life, " +
        "where X is the number of Shrines you control.\n" +
        "Whenever another Shrine you control enters, each opponent loses 1 life and you gain 1 life."

    // X = the number of Shrines you control (this permanent already counts itself on its own ETB).
    val shrinesYouControl = DynamicAmounts
        .battlefield(Player.You, GameObjectFilter.Any.withSubtype("Shrine"))
        .count()

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.LoseLife(shrinesYouControl, EffectTarget.PlayerRef(Player.EachOpponent)) then
            Effects.GainLife(shrinesYouControl)
    }

    triggeredAbility {
        trigger = Triggers.entersBattlefield(
            filter = GameObjectFilter.Any.withSubtype("Shrine").youControl(),
            binding = TriggerBinding.OTHER,
        )
        effect = Effects.LoseLife(1, EffectTarget.PlayerRef(Player.EachOpponent)) then
            Effects.GainLife(1)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "111"
        artist = "Slawek Fedorczuk"
        flavorText = "\"This is supposed to be the history of my people...\"\n—Aang"
        imageUri = "https://cards.scryfall.io/normal/front/0/6/06b7675e-e665-49cb-a8d9-7092a654d464.jpg?1764120766"
    }
}
