package com.wingedsheep.mtg.sets.definitions.inv.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantDynamicStatsEffect
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.conditions.WasKicked
import com.wingedsheep.sdk.scripting.events.CounterTypeFilter
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Kangee, Aerie Keeper
 * {2}{W}{U}
 * Legendary Creature — Bird Wizard
 * 2/2
 * Kicker {X}{2}
 * Flying
 * When Kangee enters, if it was kicked, put X feather counters on it.
 * Other Bird creatures get +1/+1 for each feather counter on Kangee.
 *
 * Kicker {X}{2} is a variable optional cost (cf. [VerdelothTheAncient]): the kicked cast prompts
 * for X, which flows to the ETB trigger via [DynamicAmount.XValue]. The lord bonus reads the live
 * feather-counter count off Kangee via [DynamicAmounts.countersOnSelf] (cf. [com.wingedsheep.mtg.sets.definitions.jud.cards.SoulcatchersAerie]),
 * scaling other Birds by that amount; `excludeSelf` keeps Kangee from pumping itself.
 */
val KangeeAerieKeeper = card("Kangee, Aerie Keeper") {
    manaCost = "{2}{W}{U}"
    colorIdentity = "WU"
    typeLine = "Legendary Creature — Bird Wizard"
    power = 2
    toughness = 2
    oracleText = "Kicker {X}{2} (You may pay an additional {X}{2} as you cast this spell.)\n" +
        "Flying\n" +
        "When Kangee enters, if it was kicked, put X feather counters on it.\n" +
        "Other Bird creatures get +1/+1 for each feather counter on Kangee."

    keywordAbility(KeywordAbility.kicker("{X}{2}"))
    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        triggerCondition = WasKicked
        effect = Effects.AddDynamicCounters(Counters.FEATHER, DynamicAmount.XValue, EffectTarget.Self)
    }

    staticAbility {
        val featherCount = DynamicAmounts.countersOnSelf(CounterTypeFilter.Named(Counters.FEATHER))
        ability = GrantDynamicStatsEffect(
            filter = GroupFilter(GameObjectFilter.Creature.withSubtype("Bird"), excludeSelf = true),
            powerBonus = featherCount,
            toughnessBonus = featherCount
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "253"
        artist = "Mark Romanoski"
        imageUri = "https://cards.scryfall.io/normal/front/3/a/3afd7e8e-4fcc-4003-9791-7baf10ef1880.jpg?1562906943"
    }
}
