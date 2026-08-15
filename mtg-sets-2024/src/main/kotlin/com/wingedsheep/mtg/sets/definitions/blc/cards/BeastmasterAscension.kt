package com.wingedsheep.mtg.sets.definitions.blc.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ConditionalStaticAbility
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.conditions.Compare
import com.wingedsheep.sdk.scripting.conditions.ComparisonOperator
import com.wingedsheep.sdk.scripting.events.CounterTypeFilter
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.DynamicAmount
import com.wingedsheep.sdk.scripting.values.EntityNumericProperty
import com.wingedsheep.sdk.scripting.values.EntityReference

/**
 * Beastmaster Ascension
 * {2}{G}
 * Enchantment
 *
 * Whenever a creature you control attacks, you may put a quest counter on Beastmaster Ascension.
 * As long as Beastmaster Ascension has seven or more quest counters on it, creatures you
 * control get +5/+5.
 */
val BeastmasterAscension = card("Beastmaster Ascension") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Enchantment"
    oracleText = "Whenever a creature you control attacks, you may put a quest counter on Beastmaster Ascension.\n" +
        "As long as Beastmaster Ascension has seven or more quest counters on it, creatures you control get +5/+5."

    triggeredAbility {
        trigger = Triggers.attacks(
            filter = GameObjectFilter.Creature.youControl(),
            binding = TriggerBinding.ANY,
        )
        optional = true
        effect = Effects.AddCounters(Counters.QUEST, 1, EffectTarget.Self)
    }

    staticAbility {
        ability = ConditionalStaticAbility(
            ability = ModifyStats(
                powerBonus = 5,
                toughnessBonus = 5,
                filter = GroupFilter.AllCreaturesYouControl
            ),
            condition = Compare(
                DynamicAmount.EntityProperty(
                    EntityReference.Source,
                    EntityNumericProperty.CounterCount(CounterTypeFilter.Named("quest"))
                ),
                ComparisonOperator.GTE,
                DynamicAmount.Fixed(7)
            )
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "118"
        artist = "Justin Gerard"
        imageUri = "https://cards.scryfall.io/normal/front/1/b/1bccf8a9-d135-431e-bcc0-15ab29f8f8cb.jpg?1721428757"

        ruling("2009-10-01", "If you attack with multiple creatures, Beastmaster Ascension's first ability triggers multiple times.")
    }
}
