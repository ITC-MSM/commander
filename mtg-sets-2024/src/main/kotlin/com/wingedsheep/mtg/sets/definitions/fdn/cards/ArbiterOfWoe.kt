package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Arbiter of Woe
 * {4}{B}{B}
 * Creature — Demon
 * 5/4
 *
 * As an additional cost to cast this spell, sacrifice a creature.
 * Flying
 * When this creature enters, each opponent discards a card and loses 2 life.
 * You draw a card and gain 2 life.
 */
val ArbiterOfWoe = card("Arbiter of Woe") {
    manaCost = "{4}{B}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Demon"
    power = 5
    toughness = 4
    oracleText = "As an additional cost to cast this spell, sacrifice a creature.\nFlying\n" +
        "When this creature enters, each opponent discards a card and loses 2 life. " +
        "You draw a card and gain 2 life."

    keywords(Keyword.FLYING)

    additionalCost(Costs.additional.SacrificePermanent(GameObjectFilter.Creature))

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.Composite(
            Effects.EachOpponentDiscards(1),
            Effects.ForEachPlayer(
                Player.EachOpponent,
                listOf(Effects.LoseLife(2, EffectTarget.Controller))
            ),
            Effects.DrawCards(1),
            Effects.GainLife(2)
        )
        description = "When this creature enters, each opponent discards a card and loses 2 life. " +
            "You draw a card and gain 2 life."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "55"
        artist = "Jim Pavelec"
        imageUri = "https://cards.scryfall.io/normal/front/b/2/b2496c4a-df03-4583-bd76-f98ed5cb61ee.jpg?1782689217"
    }
}
