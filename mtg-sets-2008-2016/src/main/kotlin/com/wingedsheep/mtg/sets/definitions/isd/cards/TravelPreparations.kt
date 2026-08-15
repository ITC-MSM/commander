package com.wingedsheep.mtg.sets.definitions.isd.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.effects.AddCountersEffect
import com.wingedsheep.sdk.scripting.effects.ForEachTargetEffect
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Travel Preparations
 * {1}{G}
 * Sorcery
 * Put a +1/+1 counter on each of up to two target creatures.
 * Flashback {1}{W} (You may cast this card from your graveyard for its flashback cost. Then exile it.)
 */
val TravelPreparations = card("Travel Preparations") {
    manaCost = "{1}{G}"
    colorIdentity = "WG"
    typeLine = "Sorcery"
    oracleText = "Put a +1/+1 counter on each of up to two target creatures.\n" +
        "Flashback {1}{W} (You may cast this card from your graveyard for its flashback cost. Then exile it.)"

    spell {
        target(
            "up to two target creatures",
            TargetCreature(count = 2, optional = true, filter = TargetFilter.Creature)
        )
        effect = ForEachTargetEffect(
            effects = listOf(
                AddCountersEffect(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.ContextTarget(0))
            )
        )
    }

    keywordAbility(KeywordAbility.flashback("{1}{W}"))

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "206"
        artist = "Vincent Proce"
        flavorText = "Visiting a shrine at the start of a journey makes the traveler more likely to finish it."
        imageUri = "https://cards.scryfall.io/normal/front/e/9/e9654ae7-af2c-4956-be3a-68befa33f523.jpg?1782714701"
    }
}
