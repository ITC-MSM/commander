package com.wingedsheep.mtg.sets.definitions.m19.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Dark-Dweller Oracle
 * {1}{R}
 * Creature — Goblin Shaman
 * 2/2
 * {1}, Sacrifice a creature: Exile the top card of your library. You may play that card this turn. (You still pay its costs. You can play a land this way only if you have an available land play remaining.)
 *
 * The effect is the standard impulse draw — [Patterns.Exile.impulse] gathers the top card,
 * moves the collection to exile and grants may-play over it with the default
 * `MayPlayExpiry.EndOfTurn` ("this turn"). "Sacrifice a creature" is not "another", so the
 * Oracle itself is a legal sacrifice: [Costs.Sacrifice] with no `excludeSelf`.
 */
val DarkDwellerOracle = card("Dark-Dweller Oracle") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Goblin Shaman"
    power = 2
    toughness = 2
    oracleText = "{1}, Sacrifice a creature: Exile the top card of your library. You may play that card this turn. (You still pay its costs. You can play a land this way only if you have an available land play remaining.)"

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{1}"), Costs.Sacrifice(GameObjectFilter.Creature))
        effect = Patterns.Exile.impulse(1)
        description = "{1}, Sacrifice a creature: Exile the top card of your library. You may play that card this turn."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "134"
        artist = "Deruchenko Alexander"
        flavorText = "Eyes are unnecessary for seeing the future."
        imageUri = "https://cards.scryfall.io/normal/front/6/9/69a57bfc-1de2-4b3a-84bc-19ec41087f0d.jpg"
    }
}
