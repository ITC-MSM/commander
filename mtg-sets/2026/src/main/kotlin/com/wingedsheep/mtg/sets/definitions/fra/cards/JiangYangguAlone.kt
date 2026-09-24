package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.events.AttackPredicate
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Jiang Yanggu, Alone — "attacks a player alone" is the lone declared attacker
 * ([AttackPredicate.Alone]) whose defender is a player: a creature you control can only attack a
 * player who is your opponent, so `attackingAnOpponent()` on the attacker filter is exactly "a
 * player" (it excludes planeswalkers and battles).
 *
 * The +1/+1 counters are counted on resolution, after the loot, so the card just discarded counts.
 */
val JiangYangguAlone = card("Jiang Yanggu, Alone") {
    manaCost = "{4}{R}"
    colorIdentity = "R"
    typeLine = "Legendary Creature — Human Berserker"
    power = 4
    toughness = 4
    oracleText = "Menace (This creature can't be blocked except by two or more creatures.)\n" +
        "Whenever a creature you control attacks a player alone, discard a card, then draw a card. Then " +
        "put a +1/+1 counter on that creature for each card you've discarded this turn."

    keywords(Keyword.MENACE)

    triggeredAbility {
        trigger = Triggers.attacks(
            filter = GameObjectFilter.Creature.youControl().attackingAnOpponent(),
            requires = setOf(AttackPredicate.Alone),
            binding = TriggerBinding.ANY,
        )
        effect = Effects.Composite(
            Patterns.Hand.discardCards(1),
            Effects.DrawCards(1),
            Effects.AddDynamicCounters(
                Counters.PLUS_ONE_PLUS_ONE,
                DynamicAmounts.cardsDiscardedThisTurn(),
                EffectTarget.TriggeringEntity
            )
        )
        description = "Whenever a creature you control attacks a player alone, discard a card, then draw a " +
            "card. Then put a +1/+1 counter on that creature for each card you've discarded this turn."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "246"
        artist = "Dmitry Burmak"
        flavorText = "His best friend was gone. Someone had to pay."
        imageUri = "https://cards.scryfall.io/normal/front/e/8/e8c1ce21-b77d-40bf-9ed1-478604e71f5f.jpg?1789014416"
        inBooster = false
    }
}
