package com.wingedsheep.mtg.sets.definitions.tmt.cards

import com.wingedsheep.sdk.core.CardType
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantDynamicStatsEffect
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.conditions.Compare
import com.wingedsheep.sdk.scripting.conditions.ComparisonOperator
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Krang, Master Mind
 * {6}{U}{U}
 * Legendary Artifact Creature — Utrom Warrior
 * 1/4
 *
 * Affinity for artifacts
 * When Krang enters, if you have fewer than four cards in hand, draw
 * cards equal to the difference.
 * Krang gets +1/+0 for each other artifact you control.
 */
val KrangMasterMind = card("Krang, Master Mind") {
    manaCost = "{6}{U}{U}"
    colorIdentity = "U"
    typeLine = "Legendary Artifact Creature — Utrom Warrior"
    oracleText = "Affinity for artifacts (This spell costs {1} less to cast for each artifact you control.)\nWhen Krang enters, if you have fewer than four cards in hand, draw cards equal to the difference.\nKrang gets +1/+0 for each other artifact you control."
    power = 1
    toughness = 4

    keywordAbility(KeywordAbility.Affinity(CardType.ARTIFACT))

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        triggerCondition = Compare(
            DynamicAmount.Count(Player.You, Zone.HAND, GameObjectFilter.Any),
            ComparisonOperator.LT,
            DynamicAmount.Fixed(4),
        )
        effect = Effects.DrawCards(
            DynamicAmount.Subtract(
                DynamicAmount.Fixed(4),
                DynamicAmount.Count(Player.You, Zone.HAND, GameObjectFilter.Any),
            )
        )
    }

    staticAbility {
        ability = GrantDynamicStatsEffect(
            filter = GroupFilter.source(),
            powerBonus = DynamicAmount.Subtract(
                DynamicAmount.AggregateBattlefield(Player.You, GameObjectFilter.Artifact),
                DynamicAmount.Fixed(1),
            ),
            toughnessBonus = DynamicAmount.Fixed(0),
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "43"
        artist = "Narendra Bintara Adi"
        imageUri = "https://cards.scryfall.io/normal/front/d/2/d27fa497-e842-4812-80fe-28517544e1c5.jpg?1760102684"
    }
}
