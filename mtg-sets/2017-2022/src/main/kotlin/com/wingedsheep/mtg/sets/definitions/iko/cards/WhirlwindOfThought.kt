package com.wingedsheep.mtg.sets.definitions.iko.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Whirlwind of Thought
 * {1}{U}{R}{W}
 * Enchantment
 *
 * Whenever you cast a noncreature spell, draw a card.
 *
 * [Triggers.YouCastNoncreature] already carries both halves of the sentence — the
 * `GameObjectFilter.Noncreature` spell filter and `Player.You` — so the trigger needs no extra
 * scoping. It fires on *cast*, not on resolution: the card is drawn even if the spell that caused
 * it is countered, and the draw trigger goes on the stack above the spell.
 */
val WhirlwindOfThought = card("Whirlwind of Thought") {
    manaCost = "{1}{U}{R}{W}"
    colorIdentity = "RUW"
    typeLine = "Enchantment"
    oracleText = "Whenever you cast a noncreature spell, draw a card."

    triggeredAbility {
        trigger = Triggers.YouCastNoncreature
        effect = Effects.DrawCards(1)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "215"
        artist = "Bram Sels"
        flavorText = "As Narset struggled to meditate, tiny dragonlings spiraled around her, conjuring thoughts of ancient clans."
        imageUri = "https://cards.scryfall.io/normal/front/d/0/d0699cbc-b499-44a6-82e1-631491aaaec6.jpg"
    }
}
