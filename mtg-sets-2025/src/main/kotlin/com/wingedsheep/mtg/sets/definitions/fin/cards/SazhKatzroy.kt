package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.AddCountersEffect
import com.wingedsheep.sdk.scripting.effects.MayEffect
import com.wingedsheep.sdk.scripting.effects.SearchDestination
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.predicates.CardPredicate
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Sazh Katzroy
 * {3}{G}
 * Legendary Creature — Human Pilot
 * 3/3
 * When Sazh Katzroy enters, you may search your library for a Bird or basic land card, reveal it,
 * put it into your hand, then shuffle.
 * Whenever Sazh Katzroy attacks, put a +1/+1 counter on target creature, then double the number of
 * +1/+1 counters on that creature.
 */
val SazhKatzroy = card("Sazh Katzroy") {
    manaCost = "{3}{G}"
    colorIdentity = "G"
    typeLine = "Legendary Creature — Human Pilot"
    oracleText = "When Sazh Katzroy enters, you may search your library for a Bird or basic land card, reveal it, put it into your hand, then shuffle.\nWhenever Sazh Katzroy attacks, put a +1/+1 counter on target creature, then double the number of +1/+1 counters on that creature."
    power = 3
    toughness = 3

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = MayEffect(
            Patterns.Library.searchLibrary(
                filter = GameObjectFilter(
                    cardPredicates = listOf(
                        CardPredicate.Or(
                            listOf(
                                CardPredicate.HasSubtype(Subtype("Bird")),
                                CardPredicate.IsBasicLand
                            )
                        )
                    )
                ),
                count = 1,
                destination = SearchDestination.HAND,
                reveal = true
            )
        )
    }

    triggeredAbility {
        trigger = Triggers.Attacks
        val t = target("target", TargetCreature(filter = TargetFilter.Creature))
        effect = Effects.Composite(
            AddCountersEffect(counterType = Counters.PLUS_ONE_PLUS_ONE, count = 1, target = t),
            Effects.DoubleCounters(Counters.PLUS_ONE_PLUS_ONE, t)
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "199"
        artist = "Colin Boyer"
        flavorText = "\"Chocobo, we just can't catch a break, can we?\""
        imageUri = "https://cards.scryfall.io/normal/front/1/e/1e2a3566-1390-457e-8077-d776a8671319.jpg?1748706504"
    }
}
