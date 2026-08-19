package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.DrawCardsEffect

/**
 * Primordial Sage
 * {4}{G}{G}
 * Creature — Spirit
 * 4/5
 *
 * Whenever you cast a creature spell, you may draw a card.
 */
val PrimordialSage = card("Primordial Sage") {
    manaCost = "{4}{G}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Spirit"
    oracleText = "Whenever you cast a creature spell, you may draw a card."
    power = 4
    toughness = 5

    triggeredAbility {
        trigger = Triggers.YouCastCreature
        optional = true
        effect = DrawCardsEffect(1)
        description = "Whenever you cast a creature spell, you may draw a card."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "177"
        artist = "Justin Sweet"
        flavorText = "For each creature that arrives in its audience, the sage imparts another piece of ancient wisdom for all to hear."
        imageUri = "https://cards.scryfall.io/normal/front/5/e/5e11e6d8-d375-4278-8cb0-94deeecaeeca.jpg?1783943634"
    }
}
