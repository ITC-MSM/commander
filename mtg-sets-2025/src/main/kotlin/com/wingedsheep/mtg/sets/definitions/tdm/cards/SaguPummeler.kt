package com.wingedsheep.mtg.sets.definitions.tdm.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.renew
import com.wingedsheep.sdk.model.Rarity

/**
 * Sagu Pummeler
 * {3}{G}
 * Creature — Beast
 * 4/4
 *
 * Reach
 * Renew — {4}{G}, Exile this card from your graveyard: Put two +1/+1 counters and a reach
 *   counter on target creature. Activate only as a sorcery.
 *
 * The renew payoff puts both counter kinds on a single target via two chained
 * [Effects.AddCounters] calls. The reach counter is a keyword counter (CR 122.1c) — the new
 * [com.wingedsheep.sdk.core.CounterType.REACH] is wired into `StateProjector.KEYWORD_COUNTER_MAP`
 * so the creature actually gains Reach from the counter via projected state.
 */
val SaguPummeler = card("Sagu Pummeler") {
    manaCost = "{3}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Beast"
    power = 4
    toughness = 4
    oracleText = "Reach\n" +
        "Renew — {4}{G}, Exile this card from your graveyard: Put two +1/+1 counters and a " +
        "reach counter on target creature. Activate only as a sorcery."

    keywords(Keyword.REACH)

    renew("{4}{G}") {
        val creature = target("creature", Targets.Creature)
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 2, creature)
            .then(Effects.AddCounters(Counters.REACH, 1, creature))
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "156"
        artist = "Francisco Badilla"
        flavorText = "Like talking to a grokmaul\n—Tarkir expression meaning \"a failure of diplomacy\""
        imageUri = "https://cards.scryfall.io/normal/front/d/e/def9cb5b-4062-481e-b682-3a30443c2e56.jpg?1743204591"
    }
}
