package com.wingedsheep.mtg.sets.definitions.sos.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.AddCountersEffect
import com.wingedsheep.sdk.scripting.events.CounterTypeFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Diary of Dreams
 * {2}
 * Artifact — Book
 *
 * Whenever you cast an instant or sorcery spell, put a page counter on this artifact.
 * {5}, {T}: Draw a card. This ability costs {1} less to activate for each page counter
 * on this artifact.
 */
val DiaryOfDreams = card("Diary of Dreams") {
    manaCost = "{2}"
    typeLine = "Artifact — Book"
    oracleText = "Whenever you cast an instant or sorcery spell, put a page counter on this artifact.\n" +
        "{5}, {T}: Draw a card. This ability costs {1} less to activate for each page counter on this artifact."

    triggeredAbility {
        trigger = Triggers.YouCastInstantOrSorcery
        effect = AddCountersEffect(Counters.PAGE, 1, EffectTarget.Self)
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{5}"), Costs.Tap)
        effect = Effects.DrawCards(1)
        // "costs {1} less to activate for each page counter on this artifact"
        genericCostReduction = DynamicAmounts.countersOnSelf(CounterTypeFilter.Named(Counters.PAGE))
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "248"
        artist = "Genel Jumalon"
        flavorText = "\"We make art. Art makes us.\"\n—Galazeth Prismari"
        imageUri = "https://cards.scryfall.io/normal/front/e/e/ee1e0a96-af80-444e-a456-5b256cf60625.jpg?1775938731"
    }
}
