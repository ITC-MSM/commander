package com.wingedsheep.mtg.sets.definitions.blc.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.conditions.Compare
import com.wingedsheep.sdk.scripting.conditions.ComparisonOperator
import com.wingedsheep.sdk.scripting.references.Player

/**
 * Thickest in the Thicket
 * {3}{G}{G}
 * Enchantment
 *
 * When this enchantment enters, put X +1/+1 counters on target creature,
 * where X is that creature's power.
 * At the beginning of your end step, draw two cards if you control the creature
 * with the greatest power or tied for the greatest power.
 */
val ThickestInTheThicket = card("Thickest in the Thicket") {
    manaCost = "{3}{G}{G}"
    colorIdentity = "G"
    typeLine = "Enchantment"
    oracleText = "When this enchantment enters, put X +1/+1 counters on target creature, " +
        "where X is that creature's power.\n" +
        "At the beginning of your end step, draw two cards if you control the creature " +
        "with the greatest power or tied for the greatest power."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val creature = target("creature", Targets.Creature)
        effect = Effects.AddDynamicCounters(
            Counters.PLUS_ONE_PLUS_ONE,
            DynamicAmounts.targetPower(0),
            creature
        )
    }

    // "you control the creature with the greatest power or tied for the greatest power"
    // ≡ you control a creature AND your max creature-power >= the global max creature-power.
    // The `ControlCreature` conjunct excludes the 0-vs-0 case when no creatures exist.
    triggeredAbility {
        trigger = Triggers.YourEndStep
        triggerCondition = Conditions.All(
            Conditions.ControlCreature,
            Compare(
                DynamicAmounts.battlefield(Player.You, GameObjectFilter.Creature).maxPower(),
                ComparisonOperator.GTE,
                DynamicAmounts.battlefield(Player.Each, GameObjectFilter.Creature).maxPower()
            )
        )
        effect = Effects.DrawCards(2)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "34"
        artist = "Michele Giorgi"
        flavorText = "No pain, no fame."
        imageUri = "https://cards.scryfall.io/normal/front/8/2/821f01e4-082e-424a-ae77-4053da893eb2.jpg?1721428307"
    }
}
