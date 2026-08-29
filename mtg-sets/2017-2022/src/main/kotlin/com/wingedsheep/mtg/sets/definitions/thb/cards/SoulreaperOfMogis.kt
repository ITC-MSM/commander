package com.wingedsheep.mtg.sets.definitions.thb.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Soulreaper of Mogis
 * {2}{B}
 * Enchantment Creature — Minotaur Shaman
 * 2/3
 *
 * {2}{B}, Sacrifice a creature: Draw a card.
 *
 * Two cost atoms in the order the card prints them, joined by [Costs.Composite]. "A creature" is
 * the unscoped creature filter — a sacrifice cost can only ever be paid with a permanent you
 * control, so no controller predicate is written.
 */
val SoulreaperOfMogis = card("Soulreaper of Mogis") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Enchantment Creature — Minotaur Shaman"
    power = 2
    toughness = 3
    oracleText = "{2}{B}, Sacrifice a creature: Draw a card."

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{2}{B}"), Costs.Sacrifice(GameObjectFilter.Creature))
        effect = Effects.DrawCards(1)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "115"
        artist = "Dmitry Burmak"
        flavorText = "\"We offer to Mogis the blood of the weak, and in return he makes us strong.\""
        imageUri = "https://cards.scryfall.io/normal/front/5/5/55e2f383-d2a0-4424-bf7a-79e82d6f691f.jpg"
    }
}
