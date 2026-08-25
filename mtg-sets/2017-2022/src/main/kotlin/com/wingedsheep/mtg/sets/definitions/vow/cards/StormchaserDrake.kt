package com.wingedsheep.mtg.sets.definitions.vow.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Stormchaser Drake
 * {1}{U}
 * Creature — Drake
 * 2/1
 *
 * Flying
 * Whenever this creature becomes the target of a spell you control, draw a card.
 *
 * The trigger fires as the spell is cast (CR 601.2c), so it resolves — and the card is drawn —
 * before the spell that targeted the Drake, and it still fires if that spell is later countered.
 */
val StormchaserDrake = card("Stormchaser Drake") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Drake"
    oracleText = "Flying\n" +
        "Whenever this creature becomes the target of a spell you control, draw a card."
    power = 2
    toughness = 1

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.BecomesTargetOfYourSpell
        effect = Effects.DrawCards(1)
        description = "Whenever this creature becomes the target of a spell you control, draw a card."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "82"
        artist = "Brent Hollowell"
        flavorText = "\"Blasted drake's interfering with the conductivity again. Garl! Go chase it away.\"\n" +
            "—Ludevic, necro-alchemist"
        imageUri = "https://cards.scryfall.io/normal/front/3/d/3dd5c860-9d27-40d9-af38-aaf40bd52423.jpg?1783924883"
    }
}
