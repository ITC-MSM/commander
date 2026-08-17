package com.wingedsheep.mtg.sets.definitions.mrd.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersWithDynamicCounters
import com.wingedsheep.sdk.scripting.conditions.Compare
import com.wingedsheep.sdk.scripting.conditions.ComparisonOperator
import com.wingedsheep.sdk.scripting.events.CounterTypeFilter
import com.wingedsheep.sdk.scripting.values.DynamicAmount
import com.wingedsheep.sdk.scripting.values.EntityNumericProperty
import com.wingedsheep.sdk.scripting.values.EntityReference

/**
 * Chalice of the Void
 * {X}{X}
 * Artifact
 *
 * This artifact enters with X charge counters on it.
 * Whenever a player casts a spell with mana value equal to the number of charge counters on this
 * artifact, counter that spell.
 */
val ChaliceOfTheVoid = card("Chalice of the Void") {
    manaCost = "{X}{X}"
    colorIdentity = ""
    typeLine = "Artifact"
    oracleText = "This artifact enters with X charge counters on it.\n" +
        "Whenever a player casts a spell with mana value equal to the number of charge counters " +
        "on this artifact, counter that spell."

    replacementEffect(
        EntersWithDynamicCounters(
            counterType = CounterTypeFilter.Named(Counters.CHARGE),
            count = DynamicAmount.CastX
        )
    )

    triggeredAbility {
        trigger = Triggers.AnyPlayerCastsSpell
        triggerRestriction = Compare(
            DynamicAmount.EntityProperty(EntityReference.Triggering, EntityNumericProperty.ManaValue),
            ComparisonOperator.EQ,
            DynamicAmounts.countersOnSelf(CounterTypeFilter.Named(Counters.CHARGE))
        )
        effect = Effects.CounterTriggeringSpell()
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "150"
        artist = "Mark Zug"
        imageUri = "https://cards.scryfall.io/normal/front/1/a/1a02ca71-5e39-4a5f-aaba-a1e3e10a6a3e.jpg?1783944526"

        ruling("2021-03-19", "A mana cost of {X}{X} means that you pay twice X. For example, if you want X to be 3, you pay {6} to cast Chalice of the Void.")
        ruling("2021-03-19", "The number of charge counters on Chalice of the Void matters only at the time the spell is cast. Changing the number of charge counters on Chalice of the Void after a spell has been cast won't change whether the ability triggers or counters the spell.")
        ruling("2021-03-19", "If there are zero charge counters on Chalice of the Void, it counters each spell with a mana value of 0. This includes face-down creature spells cast with morph's alternative cost.")
        ruling("2021-03-19", "Chalice of the Void has to be on the battlefield at the end of casting a spell for the ability to trigger. If you sacrifice Chalice of the Void as a cost to cast a spell, its ability can't trigger. However, if it leaves the battlefield once its ability has triggered, that ability will still counter the spell.")
    }
}
