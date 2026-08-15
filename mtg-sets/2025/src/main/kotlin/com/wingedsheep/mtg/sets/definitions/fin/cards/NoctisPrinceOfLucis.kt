package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersWithCounters
import com.wingedsheep.sdk.scripting.EventPattern
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.MayCastFromGraveyard
import com.wingedsheep.sdk.scripting.events.CounterTypeFilter

/**
 * Noctis, Prince of Lucis — Final Fantasy #235
 * {1}{W}{U}{B} · Legendary Creature — Human Noble · 4/3
 *
 * Lifelink
 * You may cast artifact spells from your graveyard by paying 3 life in addition to paying their
 * other costs. If you cast a spell this way, that artifact enters with a finality counter on it.
 *
 * The artifact analogue of Leonardo, Sewer Samurai: a [MayCastFromGraveyard] permission (filtered
 * to artifacts, with an additional 3-life cost) plus a non-self [EntersWithCounters] that stamps a
 * finality counter on artifacts you control that were cast from a graveyard. The default
 * `appliesTo` for [EntersWithCounters] is creatures-you-control, so it MUST be overridden to
 * artifacts-you-control here, or the replacement would never fire for non-creature artifacts.
 *
 * As with Leonardo, [Conditions.WasCastFromGraveyard] is true for any graveyard cast of a
 * you-controlled artifact, not strictly via Noctis's permission — the accepted corpus modeling for
 * "if you cast a spell this way" (it only diverges if another effect also grants artifact-from-
 * graveyard casting). The finality counter's die→exile replacement is engine-intrinsic.
 */
val NoctisPrinceOfLucis = card("Noctis, Prince of Lucis") {
    manaCost = "{1}{W}{U}{B}"
    colorIdentity = "WUB"
    typeLine = "Legendary Creature — Human Noble"
    power = 4
    toughness = 3
    oracleText = "Lifelink\n" +
        "You may cast artifact spells from your graveyard by paying 3 life in addition to paying " +
        "their other costs. If you cast a spell this way, that artifact enters with a finality counter on it."

    keywords(Keyword.LIFELINK)

    staticAbility {
        ability = MayCastFromGraveyard(
            filter = GameObjectFilter.Artifact,
            lifeCost = 3
        )
    }

    // Artifacts you control cast from the graveyard enter with a finality counter. selfOnly = false
    // targets the *other* artifact; appliesTo is overridden to artifacts (the default is creatures).
    replacementEffect(
        EntersWithCounters(
            counterType = CounterTypeFilter.Named(Counters.FINALITY),
            count = 1,
            selfOnly = false,
            condition = Conditions.WasCastFromGraveyard,
            appliesTo = EventPattern.ZoneChangeEvent(
                filter = GameObjectFilter.Artifact.youControl(),
                to = Zone.BATTLEFIELD
            )
        )
    )

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "235"
        artist = "Jeremy Chong"
        flavorText = "Many sacrificed all for the King; so must the King sacrifice himself for all."
        imageUri = "https://cards.scryfall.io/normal/front/1/8/1881a66b-956d-4bab-b578-5b2d3407c972.jpg?1782686412"
    }
}
