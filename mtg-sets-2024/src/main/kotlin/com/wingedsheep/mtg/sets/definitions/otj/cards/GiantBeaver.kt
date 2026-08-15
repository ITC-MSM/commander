package com.wingedsheep.mtg.sets.definitions.otj.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Giant Beaver
 * {3}{G}
 * Creature — Beaver Mount
 * 4/4
 *
 * Vigilance
 * Whenever this creature attacks while saddled, put a +1/+1 counter on target creature that
 * saddled it this turn.
 * Saddle 3 (Tap any number of other creatures you control with total power 3 or more: This Mount
 * becomes saddled until end of turn. Saddle only as a sorcery.)
 *
 * "While saddled" is an intervening-if (CR 603.4) on the attack trigger. The target is restricted
 * to creatures that saddled this Mount this turn via the source-relative
 * `crewedOrSaddledSourceThisTurn` filter (backed by the engine's CrewSaddleContributorsComponent).
 */
val GiantBeaver = card("Giant Beaver") {
    manaCost = "{3}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Beaver Mount"
    power = 4
    toughness = 4
    oracleText = "Vigilance\n" +
        "Whenever this creature attacks while saddled, put a +1/+1 counter on target creature that " +
        "saddled it this turn.\n" +
        "Saddle 3 (Tap any number of other creatures you control with total power 3 or more: This " +
        "Mount becomes saddled until end of turn. Saddle only as a sorcery.)"

    keywords(Keyword.VIGILANCE)
    keywordAbility(KeywordAbility.saddle(3))

    triggeredAbility {
        trigger = Triggers.Attacks
        triggerCondition = Conditions.SourceIsSaddled
        val saddler = target(
            "target creature that saddled it this turn",
            TargetCreature(filter = TargetFilter(GameObjectFilter.Creature.crewedOrSaddledSourceThisTurn()))
        )
        effect = Effects.AddCounters("+1/+1", 1, saddler)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "165"
        artist = "Lars Grant-West"
        imageUri = "https://cards.scryfall.io/normal/front/9/1/919826a9-c427-42c6-8885-a87f0b6d2192.jpg?1712355929"
    }
}
