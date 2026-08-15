package com.wingedsheep.mtg.sets.definitions.otj.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.effects.WardCost
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Armored Armadillo
 * {W}
 * Creature — Armadillo
 * 0/4
 * Ward {1}
 * {3}{W}: This creature gets +X/+0 until end of turn, where X is its toughness.
 */
val ArmoredArmadillo = card("Armored Armadillo") {
    manaCost = "{W}"
    colorIdentity = "W"
    typeLine = "Creature — Armadillo"
    power = 0
    toughness = 4
    oracleText = "Ward {1} (Whenever this creature becomes the target of a spell or ability an opponent controls, counter it unless that player pays {1}.)\n" +
        "{3}{W}: This creature gets +X/+0 until end of turn, where X is its toughness."

    keywordAbility(KeywordAbility.Ward(WardCost.Mana("{1}")))

    activatedAbility {
        cost = Costs.Mana("{3}{W}")
        effect = Effects.ModifyStats(
            power = DynamicAmounts.sourceToughness(),
            toughness = DynamicAmount.Fixed(0),
            target = EffectTarget.Self
        )
        description = "{3}{W}: This creature gets +X/+0 until end of turn, where X is its toughness."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "3"
        artist = "Leon Tukker"
        imageUri = "https://cards.scryfall.io/normal/front/2/6/263232df-69b8-4205-93ad-c724fe57ec11.jpg?1712355235"
        flavorText = "\"I always wanted to ride a slow cannonball with legs!\"\n—Kellan"
    }
}
