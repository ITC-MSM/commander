package com.wingedsheep.mtg.sets.definitions.ddq.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Topplegeist
 * {W}
 * Creature — Spirit
 * 1/1
 * Flying
 * When this creature enters, tap target creature an opponent controls.
 * Delirium — At the beginning of each opponent's upkeep, if there are four or more card types
 * among cards in your graveyard, tap target creature that player controls.
 *
 * Canonical printing is DDQ (pre-SOI).
 */
val Topplegeist = card("Topplegeist") {
    manaCost = "{W}"
    colorIdentity = "W"
    typeLine = "Creature — Spirit"
    oracleText =
        "Flying\n" +
            "When this creature enters, tap target creature an opponent controls.\n" +
            "Delirium — At the beginning of each opponent's upkeep, if there are four or more " +
            "card types among cards in your graveyard, tap target creature that player controls."
    power = 1
    toughness = 1

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val t = target(
            "target creature an opponent controls",
            TargetCreature(filter = TargetFilter.Creature.opponentControls()),
        )
        effect = Effects.Tap(t)
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
        effect = Effects.Tap(t)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "21"
        artist = "Seb McKinnon"
        imageUri =
            "https://cards.scryfall.io/normal/front/c/9/c987f24a-679f-4c4f-a04f-251cec3aef67.jpg?1783937853"
    }
}
