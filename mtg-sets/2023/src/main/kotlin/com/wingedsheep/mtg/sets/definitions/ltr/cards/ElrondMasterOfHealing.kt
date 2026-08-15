package com.wingedsheep.mtg.sets.definitions.ltr.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.ForEachTargetEffect
import com.wingedsheep.sdk.scripting.effects.MayEffect
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetCreature
import com.wingedsheep.sdk.scripting.values.ContextPropertyKey
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Elrond, Master of Healing
 * {2}{G}{U}
 * Legendary Creature — Elf Noble
 * 4/4
 *
 * Whenever you scry, put a +1/+1 counter on each of up to X target creatures,
 * where X is the number of cards looked at while scrying this way.
 * Whenever a creature you control with a +1/+1 counter on it becomes the
 * target of a spell or ability an opponent controls, you may draw a card.
 */
val ElrondMasterOfHealing = card("Elrond, Master of Healing") {
    manaCost = "{2}{G}{U}"
    colorIdentity = "GU"
    typeLine = "Legendary Creature — Elf Noble"
    power = 4
    toughness = 4
    oracleText = "Whenever you scry, put a +1/+1 counter on each of up to X target creatures, " +
        "where X is the number of cards looked at while scrying this way.\n" +
        "Whenever a creature you control with a +1/+1 counter on it becomes the target of a spell " +
        "or ability an opponent controls, you may draw a card."

    triggeredAbility {
        trigger = Triggers.WheneverYouScry
        target(
            "up to X target creatures",
            TargetCreature(
                optional = true,
                dynamicMaxCount = DynamicAmount.ContextProperty(ContextPropertyKey.TRIGGER_SCRY_COUNT)
            )
        )
        effect = ForEachTargetEffect(
            listOf(Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.ContextTarget(0)))
        )
    }

    triggeredAbility {
        trigger = Triggers.CreatureYouControlBecomesTargetByOpponent(
            GameObjectFilter.Creature.withCounter(Counters.PLUS_ONE_PLUS_ONE)
        )
        effect = MayEffect(Effects.DrawCards(1))
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "200"
        artist = "Wangjie Li"
        imageUri = "https://cards.scryfall.io/normal/front/d/2/d26ffb2c-f7a5-4a4f-9b99-c8de9dfd49da.jpg?1686969733"
    }
}
