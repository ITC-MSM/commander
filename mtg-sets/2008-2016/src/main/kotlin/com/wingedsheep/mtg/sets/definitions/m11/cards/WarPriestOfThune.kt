package com.wingedsheep.mtg.sets.definitions.m11.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * War Priest of Thune
 * {1}{W}
 * Creature — Human Cleric
 * 2/2
 *
 * When this creature enters, you may destroy target enchantment.
 *
 * The printed "you may" is the `optional = true` shorthand, which lowers to a
 * `Gate.MayDecide` around the destroy — the trigger still goes on the stack and still chooses a
 * target, and the controller only decides whether to destroy it on resolution.
 */
val WarPriestOfThune = card("War Priest of Thune") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Cleric"
    power = 2
    toughness = 2
    oracleText = "When this creature enters, you may destroy target enchantment."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        optional = true
        target = Targets.Enchantment
        effect = Effects.Destroy(EffectTarget.ContextTarget(0))
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "38"
        artist = "Izzy"
        flavorText = "\"Let nothing take away from the purity of swords clashing or of arrows taking flight toward the breast of evil.\""
        imageUri = "https://cards.scryfall.io/normal/front/d/a/da7d96db-109d-498e-ae10-1430718c33da.jpg?1783941830"
    }
}
