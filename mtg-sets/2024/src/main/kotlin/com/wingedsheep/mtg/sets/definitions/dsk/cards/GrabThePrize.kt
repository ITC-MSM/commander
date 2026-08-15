package com.wingedsheep.mtg.sets.definitions.dsk.cards

import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Grab the Prize
 * {1}{R}
 * Sorcery
 * As an additional cost to cast this spell, discard a card.
 * Draw two cards. If the discarded card wasn't a land card, Grab the Prize deals 2 damage to
 * each opponent.
 *
 * The discard is a mandatory additional cost (`Costs.additional.DiscardCards()`), so exactly one
 * card always reaches the graveyard before resolution. At resolution the conditional damage gates
 * on whether that discarded card was a land — `Conditions.DiscardedCardMatches(Land)` reads the
 * card's (now-graveyard) characteristics (CR 608.2), and `Not(...)` expresses "wasn't a land card".
 */
val GrabThePrize = card("Grab the Prize") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Sorcery"
    oracleText = "As an additional cost to cast this spell, discard a card.\n" +
        "Draw two cards. If the discarded card wasn't a land card, Grab the Prize deals 2 damage to each opponent."

    additionalCost(Costs.additional.DiscardCards())

    spell {
        effect = Effects.DrawCards(2) then
            ConditionalEffect(
                condition = Conditions.Not(Conditions.DiscardedCardMatches(GameObjectFilter.Land)),
                effect = Effects.DealDamage(2, EffectTarget.PlayerRef(Player.EachOpponent))
            )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "138"
        artist = "Halil Ural"
        flavorText = "Unfortunately, the keyhole turned out to be located in an even larger, scarier clown mouth."
        imageUri = "https://cards.scryfall.io/normal/front/5/0/50895202-f1a1-4840-a11a-55b78b8b5929.jpg?1726286361"
    }
}
