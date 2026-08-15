package com.wingedsheep.mtg.sets.definitions.ddq.cards

import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.Duration
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Tooth Collector
 * {2}{B}
 * Creature — Human Rogue
 * 3/2
 * When this creature enters, target creature an opponent controls gets -1/-1 until end of turn.
 * Delirium — At the beginning of each opponent's upkeep, if there are four or more card types
 * among cards in your graveyard, target creature that player controls gets -1/-1 until end of turn.
 *
 * Canonical printing is DDQ (pre-SOI).
 */
val ToothCollector = card("Tooth Collector") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Human Rogue"
    oracleText =
        "When this creature enters, target creature an opponent controls gets -1/-1 until end of turn.\n" +
            "Delirium — At the beginning of each opponent's upkeep, if there are four or more " +
            "card types among cards in your graveyard, target creature that player controls " +
            "gets -1/-1 until end of turn."
    power = 3
    toughness = 2

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val t = target(
            "target creature an opponent controls",
            TargetCreature(filter = TargetFilter.Creature.opponentControls()),
        )
        effect = Effects.ModifyStats(-1, -1, t, Duration.EndOfTurn)
    }

    triggeredAbility {
        trigger = Triggers.EachOpponentUpkeep
        triggerCondition = Conditions.Delirium(4)
        val t = target(
            "target creature that player controls",
            TargetCreature(
                filter = TargetFilter(GameObjectFilter.Creature.controlledByTriggeringPlayer()),
            ),
        )
        effect = Effects.ModifyStats(-1, -1, t, Duration.EndOfTurn)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "64"
        artist = "Bud Cook"
        imageUri =
            "https://cards.scryfall.io/normal/front/a/0/a0f7106b-6550-4b84-82df-98d47b823548.jpg?1783937836"
    }
}
