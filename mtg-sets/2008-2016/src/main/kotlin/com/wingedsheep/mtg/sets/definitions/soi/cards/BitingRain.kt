package com.wingedsheep.mtg.sets.definitions.soi.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.madness
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Biting Rain (Shadows over Innistrad #102)
 * {2}{B}{B}
 * Sorcery
 *
 * All creatures get -2/-2 until end of turn.
 * Madness {2}{B}
 *
 * "All creatures" is untargeted and controller-agnostic — a plain group iteration that applies
 * the -2/-2 to each iterated creature (Infest is the same shape).
 */
val BitingRain = card("Biting Rain") {
    manaCost = "{2}{B}{B}"
    colorIdentity = "B"
    typeLine = "Sorcery"
    oracleText = "All creatures get -2/-2 until end of turn.\n" +
        "Madness {2}{B} (If you discard this card, discard it into exile. When you do, cast it for its madness cost or put it into your graveyard.)"

    spell {
        effect = Effects.ForEachInGroup(
            GroupFilter(GameObjectFilter.Creature),
            Effects.ModifyStats(-2, -2, EffectTarget.Self)
        )
    }

    madness("{2}{B}")

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "102"
        artist = "John Stanko"
        flavorText = "On Innistrad, it is seldom wrong to stay indoors."
        imageUri = "https://cards.scryfall.io/normal/front/5/a/5ac62d2f-6834-4d98-b69d-bd7b5831d981.jpg?1783937780"
    }
}
