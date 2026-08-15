package com.wingedsheep.mtg.sets.definitions.spm.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.effects.AddCountersEffect
import com.wingedsheep.sdk.scripting.effects.DrawCardsEffect
import com.wingedsheep.sdk.scripting.events.DamageType
import com.wingedsheep.sdk.scripting.events.RecipientFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.predicates.StatePredicate
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * SP//dr, Piloted by Peni
 * {3}{W}{U}
 * Legendary Artifact Creature — Spider Hero, 4/4
 *
 * Vigilance
 * When SP//dr enters, put a +1/+1 counter on target creature.
 * Whenever a modified creature you control deals combat damage to a player, draw a card.
 * (Equipment, Auras you control, and counters are modifications.)
 */
val SPDrPilotedByPeni = card("SP//dr, Piloted by Peni") {
    manaCost = "{3}{W}{U}"
    colorIdentity = "WU"
    typeLine = "Legendary Artifact Creature — Spider Hero"
    oracleText = "Vigilance\nWhen SP//dr enters, put a +1/+1 counter on target creature.\nWhenever a modified creature you control deals combat damage to a player, draw a card. (Equipment, Auras you control, and counters are modifications.)"
    power = 4
    toughness = 4
    keywords(Keyword.VIGILANCE)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val t = target("target", TargetCreature(filter = TargetFilter.Creature))
        effect = AddCountersEffect(counterType = Counters.PLUS_ONE_PLUS_ONE, count = 1, target = t)
    }

    triggeredAbility {
        trigger = Triggers.dealsDamage(
            DamageType.Combat,
            RecipientFilter.AnyPlayer,
            sourceFilter = GameObjectFilter.Creature.youControl().copy(
                statePredicates = GameObjectFilter.Creature.youControl().statePredicates +
                    StatePredicate.IsModified
            ),
            binding = TriggerBinding.ANY
        )
        effect = DrawCardsEffect(1)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "147"
        artist = "Toni Infante"
        imageUri = "https://cards.scryfall.io/normal/front/c/4/c47c1d83-e76d-4939-9ed6-05a9e709dea1.jpg?1783905311"
    }
}
