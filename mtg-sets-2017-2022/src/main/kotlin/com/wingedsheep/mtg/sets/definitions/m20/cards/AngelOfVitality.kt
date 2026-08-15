package com.wingedsheep.mtg.sets.definitions.m20.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ConditionalStaticAbility
import com.wingedsheep.sdk.scripting.EventPattern
import com.wingedsheep.sdk.scripting.ModifyLifeGain
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.conditions.Compare
import com.wingedsheep.sdk.scripting.conditions.ComparisonOperator
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Angel of Vitality (M20 #4)
 * {2}{W}  Creature — Angel  2/2
 *
 * Flying
 * If you would gain life, you gain that much life plus 1 instead.
 * This creature gets +2/+2 as long as you have 25 or more life.
 */
val AngelOfVitality = card("Angel of Vitality") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Angel"
    oracleText = "Flying\n" +
        "If you would gain life, you gain that much life plus 1 instead.\n" +
        "This creature gets +2/+2 as long as you have 25 or more life."
    power = 2
    toughness = 2

    keywords(Keyword.FLYING)

    replacementEffect(
        ModifyLifeGain(
            multiplier = 1,
            modifier = 1,
            appliesTo = EventPattern.LifeGainEvent(player = Player.You)
        )
    )

    staticAbility {
        ability = ConditionalStaticAbility(
            ability = ModifyStats(
                powerBonus = 2,
                toughnessBonus = 2,
                filter = GroupFilter.source()
            ),
            condition = Compare(
                left = DynamicAmount.LifeTotal(Player.You),
                operator = ComparisonOperator.GTE,
                right = DynamicAmount.Fixed(25)
            )
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "4"
        artist = "Johannes Voss"
        imageUri = "https://cards.scryfall.io/normal/front/e/2/e2f39777-b80a-4618-9310-a9e5b91bb2a2.jpg?1782708391"
    }
}
