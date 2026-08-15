package com.wingedsheep.mtg.sets.definitions.tmt.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Leader's Talent
 * {1}{W}
 * Enchantment — Class
 *
 * (Gain the next level as a sorcery to add its ability.)
 * Whenever you attack, put a +1/+1 counter on target attacking creature.
 * {2}{W}: Level 2
 * Whenever a creature you control leaves the battlefield, if it had a counter on it, you gain 2 life.
 * {3}{W}: Level 3
 * Whenever you cast a spell, put a +1/+1 counter on each creature you control.
 */
val LeadersTalent = card("Leader's Talent") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Enchantment — Class"
    oracleText = "(Gain the next level as a sorcery to add its ability.)\n" +
        "Whenever you attack, put a +1/+1 counter on target attacking creature.\n" +
        "{2}{W}: Level 2\nWhenever a creature you control leaves the battlefield, if it had a counter on it, you gain 2 life.\n" +
        "{3}{W}: Level 3\nWhenever you cast a spell, put a +1/+1 counter on each creature you control."

    // Level 1: Whenever you attack, put a +1/+1 counter on target attacking creature.
    triggeredAbility {
        trigger = Triggers.YouAttack
        val attacker = target("target attacking creature", Targets.AttackingCreature)
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, attacker)
    }

    // Level 2: Whenever a creature you control leaves the battlefield, if it had a counter
    // on it, you gain 2 life.
    classLevel(2, "{2}{W}") {
        triggeredAbility {
            trigger = Triggers.leavesBattlefield(
                filter = GameObjectFilter.Creature.youControl(),
                binding = TriggerBinding.ANY
            )
            triggerCondition = Conditions.TriggeringEntityHadCounters
            effect = Effects.GainLife(2)
        }
    }

    // Level 3: Whenever you cast a spell, put a +1/+1 counter on each creature you control.
    classLevel(3, "{3}{W}") {
        triggeredAbility {
            trigger = Triggers.YouCastSpell
            effect = Effects.ForEachInGroup(
                filter = GroupFilter(GameObjectFilter.Creature.youControl()),
                effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.Self)
            )
        }
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "13"
        artist = "Manuel Castañón"
        imageUri = "https://cards.scryfall.io/normal/front/4/c/4cbcb622-1aff-460f-b8fa-4502d991e0ad.jpg?1777939766"
    }
}
