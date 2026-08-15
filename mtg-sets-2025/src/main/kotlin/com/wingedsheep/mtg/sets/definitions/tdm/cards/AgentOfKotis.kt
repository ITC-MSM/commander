package com.wingedsheep.mtg.sets.definitions.tdm.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.renew
import com.wingedsheep.sdk.model.Rarity

/**
 * Agent of Kotis — Tarkir: Dragonstorm #36
 * {1}{U} · Creature — Human Rogue · 2/1
 *
 * Renew — {3}{U}, Exile this card from your graveyard: Put two +1/+1 counters on target
 * creature. Activate only as a sorcery.
 *
 * The `renew(cost)` builder helper adds the display-only "Renew" keyword ability plus the
 * graveyard-only activated ability (exile this card from your graveyard as a cost, sorcery
 * timing). The payoff is a single [Effects.AddCounters] of two +1/+1 counters on one target.
 */
val AgentOfKotis = card("Agent of Kotis") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Human Rogue"
    power = 2
    toughness = 1
    oracleText = "Renew — {3}{U}, Exile this card from your graveyard: Put two +1/+1 counters " +
        "on target creature. Activate only as a sorcery."

    renew("{3}{U}") {
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 2, target("creature", Targets.Creature))
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "36"
        artist = "Matt Stewart"
        imageUri = "https://cards.scryfall.io/normal/front/8/1/812d0462-0158-467f-951d-a7a121188a10.jpg?1743204105"
    }
}
