package com.wingedsheep.mtg.sets.definitions.woe.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.conditions.Exists
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Faerie Slumber Party
 * {4}{U}{U}
 * Sorcery
 *
 * Return all creatures to their owners' hands. For each opponent who controlled a creature
 * returned this way, you create two 1/1 blue Faerie creature tokens with flying and
 * "This token can block only creatures with flying."
 *
 * Ordering is the whole difficulty. The tokens are creatures, so they must be created *after*
 * the mass bounce (created first, they would be returned themselves and cease to exist), but the
 * payoff counts a board state that the bounce has already erased. The pipeline therefore
 * snapshots the count into a number slot first, bounces, then reads the slot back:
 *
 *   storeNumber(CountPlayersWith(EachOpponent, "controls a creature")) → returnAllToHand → tokens
 *
 * [DynamicAmount.CountPlayersWith] rebinds the controller per candidate, so `Player.You` inside
 * the [Exists] refers to the opponent being tested — the Bandit's Talent idiom. Because *every*
 * creature is returned, "opponents who controlled a creature returned this way" and "opponents
 * who controlled a creature as this resolved" are the same set.
 */
val FaerieSlumberParty = card("Faerie Slumber Party") {
    manaCost = "{4}{U}{U}"
    colorIdentity = "U"
    typeLine = "Sorcery"
    oracleText = "Return all creatures to their owners' hands. For each opponent who controlled " +
        "a creature returned this way, you create two 1/1 blue Faerie creature tokens with " +
        "flying and \"This token can block only creatures with flying.\""

    spell {
        effect = Effects.Pipeline {
            val opponentsHit = storeNumber(
                DynamicAmount.CountPlayersWith(
                    scope = Player.EachOpponent,
                    condition = Exists(
                        player = Player.You,
                        zone = Zone.BATTLEFIELD,
                        filter = GameObjectFilter.Creature,
                    ),
                ),
                name = "faerieSlumberPartyOpponents",
            )
            run(Patterns.Group.returnAllToHand(GroupFilter.AllCreatures))
            run(woeFaerieToken(count = DynamicAmount.Multiply(opponentsHit.amount, 2)))
        }
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "311"
        artist = "Lie Setiawan"
        imageUri = "https://cards.scryfall.io/normal/front/f/8/f8e5de58-cc4c-40b2-94c8-4e4d7cebfaf8.jpg?1783915040"
    }
}
