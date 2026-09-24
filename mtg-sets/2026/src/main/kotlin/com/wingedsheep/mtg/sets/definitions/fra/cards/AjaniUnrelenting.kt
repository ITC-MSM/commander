package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Ajani Unrelenting
 * {4}{R}{R}
 * Legendary Planeswalker — Ajani
 * Starting Loyalty: 5
 *
 * - The first line is a triggered ability on Ajani over [Triggers.YouActivateLoyaltyAbility], so
 *   it fires for Ajani's own loyalty abilities too (Ajani is on the battlefield as the cost is
 *   paid), and for any other planeswalker you activate. The Cadet resolves before the loyalty
 *   ability that caused it, so a +1 pumps and hastes that Cadet and the −2 counts it.
 * - −2 is Change of Fortune's "discard your hand, then draw" shape: the draw count is read after
 *   the discard, as the printed "then" demands.
 * - −3 excludes only tokens *you* control: every nontoken creature, plus an opponent's tokens.
 *   That is a heterogeneous OR ([GameObjectFilter.or]) — nontoken creature, or creature an
 *   opponent controls. Ajani's own nontoken creatures are hit.
 */
val AjaniUnrelenting = card("Ajani Unrelenting") {
    manaCost = "{4}{R}{R}"
    colorIdentity = "R"
    typeLine = "Legendary Planeswalker — Ajani"
    startingLoyalty = 5
    oracleText = "Whenever you activate a loyalty ability, create a 2/2 colorless Wizard Soldier " +
        "creature token named Cadet.\n" +
        "+1: Creatures you control get +1/+0 and gain haste until end of turn.\n" +
        "−2: Discard your hand, then draw a card for each creature you control.\n" +
        "−3: Ajani deals 4 damage to each creature except for tokens you control."

    triggeredAbility {
        trigger = Triggers.YouActivateLoyaltyAbility
        effect = Effects.CreateToken(
            power = 2,
            toughness = 2,
            name = "Cadet",
            creatureTypes = setOf("Wizard", "Soldier"),
            imageUri = "https://cards.scryfall.io/normal/front/8/f/8f4534d8-2783-484f-8ebf-a47b1cc4c6df.jpg?1789734318",
        )
        description = "Whenever you activate a loyalty ability, create a 2/2 colorless Wizard " +
            "Soldier creature token named Cadet."
    }

    // +1: Creatures you control get +1/+0 and gain haste until end of turn.
    loyaltyAbility(+1) {
        effect = Patterns.Group.pumpAndGrantToAll(
            power = 1,
            toughness = 0,
            keyword = Keyword.HASTE,
            filter = GroupFilter(GameObjectFilter.Creature.youControl()),
        )
        description = "Creatures you control get +1/+0 and gain haste until end of turn."
    }

    // −2: Discard your hand, then draw a card for each creature you control.
    loyaltyAbility(-2) {
        effect = Effects.Composite(
            listOf(
                Patterns.Hand.discardHand(),
                Effects.DrawCards(DynamicAmounts.creaturesYouControl()),
            )
        )
        description = "Discard your hand, then draw a card for each creature you control."
    }

    // −3: Ajani deals 4 damage to each creature except for tokens you control.
    loyaltyAbility(-3) {
        effect = Patterns.Group.dealDamageToAll(
            4,
            GroupFilter(
                GameObjectFilter.Creature.nontoken() or GameObjectFilter.Creature.opponentControls()
            ),
        )
        description = "Ajani deals 4 damage to each creature except for tokens you control."
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "242"
        artist = "Tyler Jacobson"
        imageUri = "https://cards.scryfall.io/normal/front/b/c/bc3c096f-7f6c-474c-a2c8-75d3e6ddd6f5.jpg?1788329317"
        inBooster = false
    }
}
