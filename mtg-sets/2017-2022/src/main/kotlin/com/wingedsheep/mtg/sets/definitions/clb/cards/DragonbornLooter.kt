package com.wingedsheep.mtg.sets.definitions.clb.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Dragonborn Looter
 * {1}{U}
 * Creature — Dragon Rogue
 * 1/2
 * {1}, {T}: Draw a card, then discard a card.
 *
 * Merfolk Looter with a mana surcharge: [Patterns.Hand.loot] is the whole effect — draw one, then
 * the gather/select/move discard spine whose defaults already carry the choice prompt — under a
 * [Costs.Composite] of the mana atom and [Costs.Tap].
 */
val DragonbornLooter = card("Dragonborn Looter") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Dragon Rogue"
    power = 1
    toughness = 2
    oracleText = "{1}, {T}: Draw a card, then discard a card."

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{1}"), Costs.Tap)
        effect = Patterns.Hand.loot()
        description = "{1}, {T}: Draw a card, then discard a card."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "65"
        artist = "Julio Reyna"
        flavorText = "Erdur never thought of it as stealing. Treasure simply didn't deserve to be stuck underground where no one could admire it."
        imageUri = "https://cards.scryfall.io/normal/front/5/7/570e8aaa-5273-42f4-b151-51976ab7730c.jpg?1783922793"
    }
}
