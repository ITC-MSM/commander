package com.wingedsheep.mtg.sets.definitions.mh1.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CostZone
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.SelfAlternativeCost

/**
 * Force of Negation
 * {1}{U}{U}
 * Instant
 *
 * If it's not your turn, you may exile a blue card from your hand rather than pay this spell's
 * mana cost.
 * Counter target noncreature spell. If that spell is countered this way, exile it instead of
 * putting it into its owner's graveyard.
 *
 * The blue member of the Modern Horizons "Force" cycle, and structurally its sibling
 * [ForceOfVigor]: a free [SelfAlternativeCost] whose only cost is the non-mana
 * [Costs.additional.ExileCards] of one blue card from hand, gated by [Conditions.IsNotYourTurn].
 * The counter half is [Effects.CounterSpellToExile] — the same `CounterDestination.Exile`
 * rider Spelljack uses, minus the free-cast grant — restricted to
 * [Targets.NoncreatureSpell].
 */
val ForceOfNegation = card("Force of Negation") {
    manaCost = "{1}{U}{U}"
    colorIdentity = "U"
    typeLine = "Instant"
    oracleText = "If it's not your turn, you may exile a blue card from your hand rather than " +
        "pay this spell's mana cost.\nCounter target noncreature spell. If that spell is " +
        "countered this way, exile it instead of putting it into its owner's graveyard."

    selfAlternativeCost = SelfAlternativeCost(
        manaCost = ManaCost.parse("{0}"),
        additionalCosts = listOf(
            Costs.additional.ExileCards(
                count = 1,
                filter = GameObjectFilter.Any.withColor(Color.BLUE),
                fromZone = CostZone.HAND
            )
        ),
        condition = Conditions.IsNotYourTurn
    )

    spell {
        target = Targets.NoncreatureSpell
        effect = Effects.CounterSpellToExile()
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "52"
        artist = "Paul Scott Canavan"
        flavorText = "\"Try, if you must.\""
        imageUri = "https://cards.scryfall.io/normal/front/e/9/e9be371c-c688-44ad-ab71-bd4c9f242d58.jpg?1783933144"
    }
}
