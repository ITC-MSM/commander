package com.wingedsheep.mtg.sets.definitions.emn.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CantAttackUnless
import com.wingedsheep.sdk.scripting.CantBlockUnless
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.scripting.conditions.Compare
import com.wingedsheep.sdk.scripting.conditions.ComparisonOperator
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Lupine Prototype
 * {2}
 * Artifact Creature — Wolf Construct
 * 5/5
 *
 * This creature can't attack or block unless a player has no cards in hand.
 *
 * "A player" is existential over every player, not just controller/opponent. `Exists(Player.Any,
 * Zone.HAND, negate = true)` is the WRONG shape for this — negating the whole existential yields "no
 * player among {Any} has a card" (all hands empty), not "some player's hand is empty". The correct
 * existential-over-players-satisfying-a-per-player-condition primitive is
 * `Compare(CountPlayersWith(Player.Each, Conditions.EmptyHand), GTE, Fixed(1))`: `CountPlayersWith`
 * rebinds `Player.You` inside its condition to each candidate player in turn (see
 * `DynamicAmountEvaluator.CountPlayersWith`), so `Conditions.EmptyHand` (itself `Exists(Player.You,
 * Zone.HAND, negate = true)`) is evaluated per-player and counted. `CantAttackUnless`/`CantBlockUnless`
 * route the condition through the standard `ConditionEvaluator`, so this composes unmodified at both
 * attack- and block-restriction checks.
 */
val LupinePrototype = card("Lupine Prototype") {
    manaCost = "{2}"
    colorIdentity = ""
    typeLine = "Artifact Creature — Wolf Construct"
    oracleText = "This creature can't attack or block unless a player has no cards in hand."
    power = 5
    toughness = 5

    val aPlayerHasEmptyHand = Compare(
        DynamicAmount.CountPlayersWith(Player.Each, Conditions.EmptyHand),
        ComparisonOperator.GTE,
        DynamicAmount.Fixed(1)
    )

    staticAbility {
        ability = CantAttackUnless(aPlayerHasEmptyHand)
    }
    staticAbility {
        ability = CantBlockUnless(aPlayerHasEmptyHand)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "197"
        artist = "Deruchenko Alexander"
        flavorText = "\"Ludevic saw it in a dream and set to work immediately, every detail already known. Can such genius be taught?\"\n—Stitcher Geralf"
        imageUri = "https://cards.scryfall.io/normal/front/d/d/dd8272ca-8e3d-4980-99f2-352a1db76d74.jpg?1783937424"
    }
}
