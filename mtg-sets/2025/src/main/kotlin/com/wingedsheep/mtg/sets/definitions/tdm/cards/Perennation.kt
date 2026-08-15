package com.wingedsheep.mtg.sets.definitions.tdm.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Perennation
 * {3}{W}{B}{G}
 * Sorcery
 *
 * Return target permanent card from your graveyard to the battlefield with a hexproof counter
 * and an indestructible counter on it.
 *
 * Reanimation that staples on two keyword counters as the permanent enters. Modeled with the
 * same shape as Season of the Burrow's reanimate-with-counters mode: target a permanent card in
 * your graveyard, [Effects.PutOntoBattlefield] it (so it enters as the original object, keeping
 * the entityId), then add a HEXPROOF counter and an INDESTRUCTIBLE counter to that same object.
 * Both are keyword counters mapped in [StateProjector.KEYWORD_COUNTER_MAP], so the permanent
 * gains hexproof and indestructible via projected state for as long as the counters remain.
 */
val Perennation = card("Perennation") {
    manaCost = "{3}{W}{B}{G}"
    colorIdentity = "WBG"
    typeLine = "Sorcery"
    oracleText = "Return target permanent card from your graveyard to the battlefield with a " +
        "hexproof counter and an indestructible counter on it."

    spell {
        val returnTarget = target(
            "target permanent card from your graveyard",
            TargetObject(
                filter = TargetFilter(
                    GameObjectFilter.Permanent.ownedByYou(),
                    zone = Zone.GRAVEYARD
                )
            )
        )
        effect = Effects.PutOntoBattlefield(returnTarget)
            .then(Effects.AddCounters(Counters.HEXPROOF, 1, EffectTarget.ContextTarget(0)))
            .then(Effects.AddCounters(Counters.INDESTRUCTIBLE, 1, EffectTarget.ContextTarget(0)))
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "212"
        artist = "Eli Minaya"
        flavorText = "The Abzan believe in perennation—death is not only an end, but a return " +
            "to the beginning."
        imageUri = "https://cards.scryfall.io/normal/front/f/f/ffe7071e-a214-44e8-a571-129f0db44f76.jpg?1743204835"
    }
}
