package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.DynamicAmount
import com.wingedsheep.sdk.scripting.values.EntityNumericProperty
import com.wingedsheep.sdk.scripting.values.EntityReference

/**
 * Hamletback Goliath
 * {6}{R}
 * Creature — Giant Warrior
 * 6/6
 * Whenever another creature enters, you may put X +1/+1 counters on this creature, where X is
 * that creature's power.
 *
 * "Another creature" here means *any* creature, including an opponent's — so this is the bare
 * `Triggers.entersBattlefield(Creature, OTHER)` factory, not `Triggers.OtherCreatureEnters`,
 * which carries a "you control" clause the printed text doesn't have.
 */
val HamletbackGoliath = card("Hamletback Goliath") {
    manaCost = "{6}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Giant Warrior"
    power = 6
    toughness = 6
    oracleText = "Whenever another creature enters, you may put X +1/+1 counters on this creature, " +
        "where X is that creature's power."

    triggeredAbility {
        trigger = Triggers.entersBattlefield(
            filter = GameObjectFilter.Creature,
            binding = TriggerBinding.OTHER
        )
        optional = true
        effect = Effects.AddDynamicCounters(
            Counters.PLUS_ONE_PLUS_ONE,
            DynamicAmount.EntityProperty(EntityReference.Triggering, EntityNumericProperty.Power),
            EffectTarget.Self
        )
        description = "you may put X +1/+1 counters on this creature, where X is that creature's power."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "173"
        artist = "Paolo Parente & Brian Snõddy"
        flavorText = "\"If you live on a giant's back, there's only one individual you'll ever need to fear.\"\n—Gaddock Teeg"
        imageUri = "https://cards.scryfall.io/normal/front/9/6/96f71692-6389-462f-933e-b18b5aa7d76b.jpg?1783942876"
    }
}
