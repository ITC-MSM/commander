package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.CollectionFilter
import com.wingedsheep.sdk.scripting.effects.ConditionalOnCollectionEffect
import com.wingedsheep.sdk.scripting.effects.FilterCollectionEffect
import com.wingedsheep.sdk.scripting.effects.ForEachTargetEffect
import com.wingedsheep.sdk.scripting.effects.ReflexiveTriggerEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetCreature
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Seasoned Cryomancer — "When you discard one or more nonland cards this way" is a reflexive
 * trigger (a separate stack object whose targets are chosen as it is put on the stack). It only
 * exists when at least one nonland card was actually discarded, so it is gated on the discarded
 * pile containing a nonland card; its action partitions that pile, and "up to that many target
 * creatures" is capped by the nonland count the action stored (`discardedNonland_count`).
 */
val SeasonedCryomancer = card("Seasoned Cryomancer") {
    manaCost = "{1}{U}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Human Wizard"
    power = 2
    toughness = 2
    oracleText = "When this creature enters, draw two cards, then discard two cards. When you discard " +
        "one or more nonland cards this way, tap up to that many target creatures and put a stun " +
        "counter on each of them.\n" +
        "{3}{U}{U}, Exile this card from your graveyard: Draw two cards."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.Composite(
            Effects.DrawCards(2),
            Patterns.Hand.discardCards(2),
            ConditionalOnCollectionEffect(
                collection = "discarded",
                filter = GameObjectFilter.Nonland,
                ifNotEmpty = ReflexiveTriggerEffect(
                    action = FilterCollectionEffect(
                        from = "discarded",
                        filter = CollectionFilter.MatchesFilter(GameObjectFilter.Nonland),
                        storeMatching = "discardedNonland"
                    ),
                    optional = false,
                    reflexiveEffect = ForEachTargetEffect(
                        listOf(
                            Effects.Tap(EffectTarget.ContextTarget(0)),
                            Effects.AddCounters(Counters.STUN, 1, EffectTarget.ContextTarget(0))
                        )
                    ),
                    reflexiveTargetRequirements = listOf(
                        TargetCreature(
                            optional = true,
                            dynamicMaxCount = DynamicAmount.VariableReference("discardedNonland_count")
                        )
                    ),
                    descriptionOverride = "When you discard one or more nonland cards this way, tap up " +
                        "to that many target creatures and put a stun counter on each of them."
                )
            )
        )
        description = "When this creature enters, draw two cards, then discard two cards. When you " +
            "discard one or more nonland cards this way, tap up to that many target creatures and put " +
            "a stun counter on each of them."
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{3}{U}{U}"), Costs.ExileSelf)
        effect = Effects.DrawCards(2)
        activateFromZone = Zone.GRAVEYARD
        description = "{3}{U}{U}, Exile this card from your graveyard: Draw two cards."
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "38"
        artist = "Andrea Piparo"
        imageUri = "https://cards.scryfall.io/normal/front/5/1/51d86875-420d-4e82-b69c-4feeb99c9428.jpg?1789470781"
        inBooster = false
    }
}
