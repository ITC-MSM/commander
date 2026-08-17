package com.wingedsheep.mtg.sets.definitions.afr.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Priest of Ancient Lore
 * {2}{W}
 * Creature — Dwarf Cleric
 * 2/1
 * When this creature enters, you gain 1 life and draw a card.
 *
 * Two independent rewards joined by "and", so the body is [Effects.Composite] over [Effects.GainLife]
 * and [Effects.DrawCards] in printed order — both default to the controller, which is who the line means.
 */
val PriestOfAncientLore = card("Priest of Ancient Lore") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Dwarf Cleric"
    power = 2
    toughness = 1
    oracleText = "When this creature enters, you gain 1 life and draw a card."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.Composite(
            Effects.GainLife(1),
            Effects.DrawCards(1),
        )
        description = "When this creature enters, you gain 1 life and draw a card."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "35"
        artist = "Jarel Threat"
        flavorText = "\"We are a product of our ancestors, the apotheosis of countless noble generations. Speak their names with reverence, and they will guide your path.\""
        imageUri = "https://cards.scryfall.io/normal/front/b/1/b10caff0-701a-48d8-a943-f947482e795a.jpg?1783926524"
    }
}
