package com.wingedsheep.mtg.sets.definitions.spm.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.AddCountersEffect
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.predicates.StatePredicate
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Silver Sable, Mercenary Leader
 * {2}{W}
 * Legendary Creature — Human Mercenary Hero, 2/3
 * When Silver Sable enters, put a +1/+1 counter on another target creature.
 * Whenever Silver Sable attacks, target modified creature you control gains lifelink until end of turn.
 * (Equipment, Auras you control, and counters are modifications.)
 */
val SilverSableMercenaryLeader = card("Silver Sable, Mercenary Leader") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Legendary Creature — Human Mercenary Hero"
    oracleText = "When Silver Sable enters, put a +1/+1 counter on another target creature.\nWhenever Silver Sable attacks, target modified creature you control gains lifelink until end of turn. (Equipment, Auras you control, and counters are modifications.)"
    power = 2
    toughness = 3

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val t = target("target", TargetCreature(filter = TargetFilter.OtherCreature))
        effect = AddCountersEffect(counterType = Counters.PLUS_ONE_PLUS_ONE, count = 1, target = t)
    }
    triggeredAbility {
        trigger = Triggers.Attacks
        val t = target(
            "target",
            TargetCreature(
                filter = TargetFilter(
                    baseFilter = GameObjectFilter.Creature.youControl().copy(
                        statePredicates = GameObjectFilter.Creature.youControl().statePredicates +
                            StatePredicate.IsModified
                    )
                )
            )
        )
        effect = Effects.GrantKeyword(Keyword.LIFELINK, t)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "13"
        artist = "JB Casacop"
        flavorText = "\"I have visual on the target. Wild Pack, provide cover—I'm going in!\""
        imageUri = "https://cards.scryfall.io/normal/front/c/f/cf0d4116-acee-4d9a-985c-396d10e03838.jpg?1757376820"
    }
}
