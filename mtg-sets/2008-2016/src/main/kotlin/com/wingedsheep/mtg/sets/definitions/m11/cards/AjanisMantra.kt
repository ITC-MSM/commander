package com.wingedsheep.mtg.sets.definitions.m11.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Ajani's Mantra
 * {1}{W}
 * Enchantment
 *
 * At the beginning of your upkeep, you may gain 1 life.
 *
 * [Triggers.YourUpkeep] is `StepEvent(Step.UPKEEP, Player.You)` with `TriggerBinding.ANY` — the
 * enchantment's own controller's upkeep. The printed "you may" is the builder's `optional = true`,
 * which lowers to a `Gate.MayDecide` around the life gain rather than living beside it as a flag
 * (same shape as Angel's Feather). [Effects.GainLife]'s default recipient is the ability's
 * controller, which is who "you" means here.
 */
val AjanisMantra = card("Ajani's Mantra") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Enchantment"
    oracleText = "At the beginning of your upkeep, you may gain 1 life."

    triggeredAbility {
        trigger = Triggers.YourUpkeep
        optional = true
        effect = Effects.GainLife(1)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "2"
        artist = "James Paick"
        flavorText = "\"He hasn't returned to the Cloud Forest. But I can still sense his calming presence.\"\n" +
            "—Zaliki of Naya"
        imageUri = "https://cards.scryfall.io/normal/front/0/8/089b1057-ed1f-45a5-ba95-28aa51713764.jpg?1783941838"
    }
}
