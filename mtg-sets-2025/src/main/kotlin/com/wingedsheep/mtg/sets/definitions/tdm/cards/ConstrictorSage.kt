package com.wingedsheep.mtg.sets.definitions.tdm.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.renew
import com.wingedsheep.sdk.model.Rarity

/**
 * Constrictor Sage — Tarkir: Dragonstorm #39
 * {4}{U} · Creature — Snake Wizard · 4/4
 *
 * When this creature enters, tap target creature an opponent controls and put a stun
 * counter on it.
 * Renew — {2}{U}, Exile this card from your graveyard: Tap target creature an opponent
 * controls and put a stun counter on it. Activate only as a sorcery.
 *
 * Both the ETB and the Renew ability are the same tap + stun-counter shape, composed from
 * [Effects.Tap] and [Effects.AddCounters]. A stun counter makes the next untap removal-only
 * (CR 122.1c / the stun counter replacement), keeping the creature tapped one extra cycle.
 */
val ConstrictorSage = card("Constrictor Sage") {
    manaCost = "{4}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Snake Wizard"
    power = 4
    toughness = 4
    oracleText = "When this creature enters, tap target creature an opponent controls and " +
        "put a stun counter on it. (If a permanent with a stun counter would become untapped, " +
        "remove one from it instead.)\n" +
        "Renew — {2}{U}, Exile this card from your graveyard: Tap target creature an opponent " +
        "controls and put a stun counter on it. Activate only as a sorcery."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val creature = target("creature", Targets.CreatureOpponentControls)
        effect = Effects.Tap(creature)
            .then(Effects.AddCounters(Counters.STUN, 1, creature))
        description = "When this creature enters, tap target creature an opponent controls " +
            "and put a stun counter on it."
    }

    renew("{2}{U}") {
        val creature = target("creature", Targets.CreatureOpponentControls)
        effect = Effects.Tap(creature)
            .then(Effects.AddCounters(Counters.STUN, 1, creature))
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "39"
        artist = "Nereida"
        imageUri = "https://cards.scryfall.io/normal/front/b/2/b2f160d7-c832-4b83-8f2e-aaeb190add3f.jpg?1743233749"
        ruling("2025-04-04", "If a card with a renew ability is put into your graveyard during your turn, you can activate that ability if it's legal to do so before any other player can take any actions.")
    }
}
