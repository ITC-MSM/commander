package com.wingedsheep.mtg.sets.definitions.znr.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersWithCounters
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.conditions.WasKicked
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Gnarlid Colony
 * {1}{G}
 * Creature — Beast
 * 2/2
 *
 * Kicker {2}{G}
 * If this creature was kicked, it enters with two +1/+1 counters on it.
 * Each creature you control with a +1/+1 counter on it has trample.
 *
 * - The kicked-counter clause is a self-only [EntersWithCounters] replacement gated on
 *   [WasKicked], so Gnarlid enters *already* carrying its two counters (CR 121 / 614) — which
 *   matters because the trample-granting static below reads counters in projected state.
 * - "Each creature you control with a +1/+1 counter on it has trample" is a [GrantKeyword] static
 *   over `Creature.youControl().withCounter(+1/+1)` (default `excludeSelf = false`, so a kicked
 *   Gnarlid grants trample to itself too).
 */
val GnarlidColony = card("Gnarlid Colony") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Beast"
    power = 2
    toughness = 2
    oracleText = "Kicker {2}{G} (You may pay an additional {2}{G} as you cast this spell.)\n" +
        "If this creature was kicked, it enters with two +1/+1 counters on it.\n" +
        "Each creature you control with a +1/+1 counter on it has trample. (It can deal excess " +
        "combat damage to the player or planeswalker it's attacking.)"

    keywordAbility(KeywordAbility.kicker("{2}{G}"))

    // If this creature was kicked, it enters with two +1/+1 counters on it.
    replacementEffect(
        EntersWithCounters(
            count = 2,
            selfOnly = true,
            condition = WasKicked,
        )
    )

    // Each creature you control with a +1/+1 counter on it has trample.
    staticAbility {
        ability = GrantKeyword(
            Keyword.TRAMPLE,
            filter = GroupFilter(
                GameObjectFilter.Creature.youControl().withCounter(Counters.PLUS_ONE_PLUS_ONE),
            ),
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "185"
        artist = "Izzy"
        flavorText = "Where mana flows, the gnarlids follow."
        imageUri = "https://cards.scryfall.io/normal/front/7/3/7327289d-eed8-44b1-8495-7172e2b49d5f.jpg?1782706243"
    }
}
