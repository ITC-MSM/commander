package com.wingedsheep.mtg.sets.definitions.sos.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.TargetPlayer
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Mathemagics
 * {X}{X}{U}{U}
 * Sorcery
 *
 * Target player draws 2ˣ cards. (2⁰ = 1, 2¹ = 2, 2² = 4, 2³ = 8, 2⁴ = 16, 2⁵ = 32, and so on.)
 *
 * The number of cards drawn is `2^X`, where X is the value chosen at cast time. Modelled with
 * [DynamicAmount.Power] over [DynamicAmount.XValue]; X = 0 draws 2⁰ = 1.
 */
val Mathemagics = card("Mathemagics") {
    manaCost = "{X}{X}{U}{U}"
    colorIdentity = "U"
    typeLine = "Sorcery"
    oracleText = "Target player draws 2ˣ cards. (2⁰ = 1, 2¹ = 2, 2² = 4, 2³ = 8, 2⁴ = 16, 2⁵ = 32, and so on.)"

    spell {
        val targetPlayer = target("target player", TargetPlayer())
        effect = Effects.DrawCards(
            count = DynamicAmount.Power(base = 2, exponent = DynamicAmount.XValue),
            target = targetPlayer
        )
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "58"
        artist = "Liiga Smilshkalne"
        flavorText = "\"Theory? Meet practice.\"\n—Tam, Quandrix second-year"
        imageUri = "https://cards.scryfall.io/normal/front/c/d/cd3cc172-5609-4bc8-9d84-50680fed6df9.jpg?1777528360"
    }
}
