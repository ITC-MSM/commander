package com.wingedsheep.mtg.sets.definitions.msh.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Kid Loki — Marvel Super Heroes #63
 * {U} · Legendary Creature — God Hero Villain · 1/1
 *
 * Each creature you control that you've put one or more +1/+1 counters on this turn has hexproof.
 * Whenever you draw your second card each turn, put a +1/+1 counter on Kid Loki.
 *
 * Modeling notes:
 *  - The hexproof grant is a plain group static — [GrantKeyword] over "creatures you control" narrowed
 *    by [com.wingedsheep.sdk.scripting.predicates.StatePredicate.ReceivedCounterThisTurn], the
 *    filter-level "you've put one or more +1/+1 counters on it this turn" predicate. Both narrowing
 *    parameters are set because the printed text uses both: `counterType` for "**+1/+1** counters" (a
 *    stun counter must not confer hexproof) and `placedByController` for "**you've** put" (an opponent
 *    proliferating your creature must not either).
 *  - The predicate reads a per-turn marker recorded at *placement* time, so hexproof survives the
 *    counters being removed later in the turn — the card asks what you put on the creature this turn,
 *    not what is on it now — and switches off at end-of-turn cleanup.
 *  - Kid Loki's own trigger feeds the static: the +1/+1 counter it puts on itself makes Kid Loki one of
 *    the creatures the first ability protects, since the static's filter includes the source.
 *  - The draw trigger is the shared [Triggers.NthCardDrawn] detector (n = 2), which fires exactly once
 *    per turn, including when a single multi-card draw crosses the threshold (CR 121.2).
 */
val KidLoki = card("Kid Loki") {
    manaCost = "{U}"
    colorIdentity = "U"
    typeLine = "Legendary Creature — God Hero Villain"
    power = 1
    toughness = 1
    oracleText = "Each creature you control that you've put one or more +1/+1 counters on this turn " +
        "has hexproof.\n" +
        "Whenever you draw your second card each turn, put a +1/+1 counter on Kid Loki."

    staticAbility {
        ability = GrantKeyword(
            keyword = Keyword.HEXPROOF,
            filter = Filters.Group.creatures {
                youControl().receivedCounterThisTurn(
                    counterType = Counters.PLUS_ONE_PLUS_ONE,
                    placedByController = true,
                )
            },
        )
    }

    triggeredAbility {
        trigger = Triggers.NthCardDrawn(2)
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.Self)
        description = "Whenever you draw your second card each turn, put a +1/+1 counter on Kid Loki."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "63"
        artist = "Mintautas Šukys"
        flavorText = "\"A lie is nothing but a story. We can choose to tell a different one.\""
        imageUri = "https://cards.scryfall.io/normal/front/5/c/5c9ff69e-6489-49f3-b401-64856f0b7c11.jpg?1783902956"
    }
}
