package com.wingedsheep.mtg.sets.definitions.zen.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.conditions.Compare
import com.wingedsheep.sdk.scripting.conditions.ComparisonOperator
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Scute Mob
 * {G}
 * Creature — Insect
 * 1/1
 * At the beginning of your upkeep, if you control five or more lands, put four +1/+1 counters on this creature.
 *
 * Intervening-if (CR 603.4): the land count is checked both when the trigger would fire at the
 * beginning of upkeep and again as it resolves.
 */
val ScuteMob = card("Scute Mob") {
    manaCost = "{G}"
    colorIdentity = "G"
    typeLine = "Creature — Insect"
    power = 1
    toughness = 1
    oracleText = "At the beginning of your upkeep, if you control five or more lands, put four +1/+1 counters on this creature."

    triggeredAbility {
        trigger = Triggers.YourUpkeep
        interveningIf = Compare(
            DynamicAmounts.battlefield(Player.You, GameObjectFilter.Land).count(),
            ComparisonOperator.GTE,
            DynamicAmount.Fixed(5),
        )
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 4, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "182"
        artist = "Zoltan Boros & Gabor Szikszai"
        flavorText = "\"Survival rule 781: There are always more scute bugs.\"\n—Zurdi, goblin shortcutter"
        imageUri = "https://cards.scryfall.io/normal/front/4/3/43e2fcc6-96d3-45f5-b4dd-e049d9ec6cec.jpg"
    }
}
