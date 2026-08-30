package com.wingedsheep.mtg.sets.definitions.avr.cards

import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.Gate
import com.wingedsheep.sdk.scripting.effects.GatedEffect
import com.wingedsheep.sdk.scripting.effects.PayManaCostEffect

/**
 * Lunar Mystic
 * {2}{U}{U}
 * Creature — Human Wizard
 * 2 / 2
 *
 * Whenever you cast an instant spell, you may pay {1}. If you do, draw a card.
 *
 * "You may pay {1}. If you do" is a [Gate.MayPay] — the payment and the draw are one resolution,
 * not a reflexive trigger.
 */
val LunarMystic = card("Lunar Mystic") {
    manaCost = "{2}{U}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Human Wizard"
    power = 2
    toughness = 2
    oracleText = "Whenever you cast an instant spell, you may pay {1}. If you do, draw a card."

    triggeredAbility {
        trigger = Triggers.youCastSpell(spellFilter = GameObjectFilter.Instant)
        effect = GatedEffect(
            gate = Gate.MayPay(PayManaCostEffect(ManaCost.parse("{1}"))),
            then = Effects.DrawCards(1)
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "65"
        artist = "Wesley Burt"
        flavorText = "\"I'm pleased this world has learned the moon affects more than the tides.\"\n—Tamiyo, the Moon Sage"
        imageUri = "https://cards.scryfall.io/normal/front/f/3/f346d236-528c-4164-9995-74cdc56597a9.jpg?1783940715"
    }
}
