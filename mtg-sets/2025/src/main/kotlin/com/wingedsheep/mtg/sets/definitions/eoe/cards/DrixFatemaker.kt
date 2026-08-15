package com.wingedsheep.mtg.sets.definitions.eoe.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Drix Fatemaker
 * {3}{G}
 * Creature — Drix Wizard
 * 3/2
 *
 * When this creature enters, put a +1/+1 counter on target creature.
 * Each creature you control with a +1/+1 counter on it has trample.
 * Warp {1}{G}
 */
val DrixFatemaker = card("Drix Fatemaker") {
    manaCost = "{3}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Drix Wizard"
    power = 3
    toughness = 2
    oracleText = "When this creature enters, put a +1/+1 counter on target creature.\n" +
        "Each creature you control with a +1/+1 counter on it has trample.\n" +
        "Warp {1}{G} (You may cast this card from your hand for its warp cost. Exile this creature at " +
        "the beginning of the next end step, then you may cast it from exile on a later turn.)"

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val t = target("target creature", Targets.Creature)
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, t)
        description = "When this creature enters, put a +1/+1 counter on target creature."
    }

    staticAbility {
        ability = GrantKeyword(
            Keyword.TRAMPLE,
            GroupFilter(GameObjectFilter.Creature.youControl().withCounter(Counters.PLUS_ONE_PLUS_ONE))
        )
    }

    warp = "{1}{G}"

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "178"
        artist = "Anna Pavleeva"
        imageUri = "https://cards.scryfall.io/normal/front/1/b/1beb7566-305e-4091-bdc4-cf4c789ac05a.jpg?1752947281"
    }
}
