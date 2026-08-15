package com.wingedsheep.mtg.sets.definitions.tmt.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.AddCountersEffect
import com.wingedsheep.sdk.scripting.effects.CreateTokenEffect
import com.wingedsheep.sdk.scripting.effects.RepeatDynamicTimesEffect
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.predicates.CardPredicate
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Sally Pride, Lioness Leader
 * {3}{W}{W}
 * Legendary Creature — Cat Mutant Rebel
 * 2/4
 *
 * When Sally Pride enters, create X 2/2 red Mutant creature tokens,
 * where X is the number of nontoken creatures you control.
 * Whenever Sally Pride attacks, put a +1/+1 counter on each creature
 * you control.
 */
val SallyPrideLionessLeader = card("Sally Pride, Lioness Leader") {
    manaCost = "{3}{W}{W}"
    colorIdentity = "W"
    typeLine = "Legendary Creature — Cat Mutant Rebel"
    oracleText = "When Sally Pride enters, create X 2/2 red Mutant creature tokens, where X is the number of nontoken creatures you control.\nWhenever Sally Pride attacks, put a +1/+1 counter on each creature you control."
    power = 2
    toughness = 4

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = RepeatDynamicTimesEffect(
            amount = DynamicAmount.Count(
                player = Player.You,
                zone = Zone.BATTLEFIELD,
                filter = GameObjectFilter.Creature.copy(
                    cardPredicates = GameObjectFilter.Creature.cardPredicates +
                        CardPredicate.IsNontoken
                ).youControl()
            ),
            body = CreateTokenEffect(
                power = 2,
                toughness = 2,
                colors = setOf(Color.RED),
                creatureTypes = setOf("Mutant"),
                imageUri = "https://cards.scryfall.io/normal/front/5/1/51e33613-7a24-461c-8d9f-12680af4b92a.jpg?1771590526"
            )
        )
    }

    triggeredAbility {
        trigger = Triggers.Attacks
        effect = Effects.ForEachInGroup(
            filter = GroupFilter(GameObjectFilter.Creature.youControl()),
            effect = AddCountersEffect(
                counterType = Counters.PLUS_ONE_PLUS_ONE,
                count = 1,
                target = EffectTarget.Self
            )
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "24"
        artist = "Andrea Tentori Montalto"
        flavorText = "\"I wanna make things better for everyone. No more squalor. No more fear.\""
        imageUri = "https://cards.scryfall.io/normal/front/b/c/bcd8ee6b-7142-4548-8b7a-691a36411851.jpg?1769005568"
    }
}
