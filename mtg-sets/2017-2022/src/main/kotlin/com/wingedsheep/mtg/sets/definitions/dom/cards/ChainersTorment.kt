package com.wingedsheep.mtg.sets.definitions.dom.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Chainer's Torment
 * {3}{B}
 * Enchantment — Saga
 *
 * (As this Saga enters and after your draw step, add a lore counter. Sacrifice after III.)
 * I, II — Chainer's Torment deals 2 damage to each opponent and you gain 2 life.
 * III — Create an X/X black Nightmare Horror creature token, where X is half your life total,
 *        rounded up. It deals X damage to you.
 */
val ChainersTorment = card("Chainer's Torment") {
    manaCost = "{3}{B}"
    colorIdentity = "B"
    typeLine = "Enchantment — Saga"
    oracleText = "(As this Saga enters and after your draw step, add a lore counter. Sacrifice after III.)\n" +
        "I, II — This Saga deals 2 damage to each opponent and you gain 2 life.\n" +
        "III — Create an X/X black Nightmare Horror creature token, where X is half your life total, " +
        "rounded up. It deals X damage to you."

    val halfLifeRoundedUp = DynamicAmount.Divide(
        numerator = DynamicAmount.YourLifeTotal,
        denominator = DynamicAmount.Fixed(2),
        roundUp = true
    )

    sagaChapter(1) {
        effect = Effects.DealDamage(2, EffectTarget.PlayerRef(Player.EachOpponent))
            .then(Effects.GainLife(2))
    }

    sagaChapter(2) {
        effect = Effects.DealDamage(2, EffectTarget.PlayerRef(Player.EachOpponent))
            .then(Effects.GainLife(2))
    }

    sagaChapter(3) {
        effect = Effects.CreateDynamicToken(
            dynamicPower = halfLifeRoundedUp,
            dynamicToughness = halfLifeRoundedUp,
            colors = setOf(Color.BLACK),
            creatureTypes = setOf("Nightmare", "Horror")
        ).then(
            Effects.DealDamage(halfLifeRoundedUp, EffectTarget.Controller)
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "82"
        artist = "Vincent Proce"
        imageUri = "https://cards.scryfall.io/normal/front/f/4/f42beafb-2fba-482c-a86a-7d668fa7cb4b.jpg?1562745605"
        ruling("2018-04-27", "If another effect causes the Nightmare Horror token's power or toughness to be a number other than X immediately after it enters the battlefield, the amount of damage it deals to you is still X, not its modified power or toughness.")
        ruling("2018-04-27", "If an effect such as that of Anointed Procession causes the final chapter ability of Chainer's Torment to create two Nightmare Horror tokens, each will deal X damage to you.")
    }
}
