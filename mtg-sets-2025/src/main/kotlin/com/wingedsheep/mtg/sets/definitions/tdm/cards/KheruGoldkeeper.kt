package com.wingedsheep.mtg.sets.definitions.tdm.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.renew
import com.wingedsheep.sdk.model.Rarity

/**
 * Kheru Goldkeeper — Tarkir: Dragonstorm #199
 * {1}{B}{G}{U} · Creature — Dragon · 3/3
 *
 * Flying
 * Whenever one or more cards leave your graveyard during your turn, create a Treasure token.
 * Renew — {2}{B}{G}{U}, Exile this card from your graveyard: Put two +1/+1 counters and a
 *   flying counter on target creature. Activate only as a sorcery.
 *
 * The leave-graveyard trigger reuses the batching [Triggers.CardsLeaveYourGraveyard] (fires once
 * per event batch) gated on [Conditions.IsYourTurn] for the "during your turn" restriction — the
 * same composition as Attuned Hunter. The renew payoff puts both counter kinds on a single target
 * via two chained [Effects.AddCounters] calls; the flying counter is a keyword counter (CR 122.1c),
 * already wired into `StateProjector.KEYWORD_COUNTER_MAP` so the creature gains Flying via projection.
 */
val KheruGoldkeeper = card("Kheru Goldkeeper") {
    manaCost = "{1}{B}{G}{U}"
    colorIdentity = "BGU"
    typeLine = "Creature — Dragon"
    power = 3
    toughness = 3
    oracleText = "Flying\n" +
        "Whenever one or more cards leave your graveyard during your turn, create a Treasure token. " +
        "(It's an artifact with \"{T}, Sacrifice this token: Add one mana of any color.\")\n" +
        "Renew — {2}{B}{G}{U}, Exile this card from your graveyard: Put two +1/+1 counters and a " +
        "flying counter on target creature. Activate only as a sorcery."

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.CardsLeaveYourGraveyard()
        triggerCondition = Conditions.IsYourTurn
        effect = Effects.CreateTreasure(1)
    }

    renew("{2}{B}{G}{U}") {
        val creature = target("creature", Targets.Creature)
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 2, creature)
            .then(Effects.AddCounters(Counters.FLYING, 1, creature))
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "199"
        artist = "Randy Vargas"
        imageUri = "https://cards.scryfall.io/normal/front/8/d/8d11183a-57f5-4ddb-8a6e-15fff704b114.jpg?1743204780"
        ruling("2025-04-04", "If multiple cards leave your graveyard at the same time, Kheru Goldkeeper's triggered ability will trigger only once.")
        ruling("2025-04-04", "If a card with a renew ability is put into your graveyard during your turn, you can activate that ability if it's legal to do so before any other player can take any actions.")
    }
}
