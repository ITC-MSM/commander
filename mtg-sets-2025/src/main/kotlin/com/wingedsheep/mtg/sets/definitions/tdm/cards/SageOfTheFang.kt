package com.wingedsheep.mtg.sets.definitions.tdm.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.renew
import com.wingedsheep.sdk.model.Rarity

/**
 * Sage of the Fang — Tarkir: Dragonstorm #155
 * {2}{G} · Creature — Human Druid · 2/2
 *
 * When this creature enters, put a +1/+1 counter on target creature.
 * Renew — {3}{G}, Exile this card from your graveyard: Put a +1/+1 counter on target creature,
 * then double the number of +1/+1 counters on that creature. Activate only as a sorcery.
 *
 * ETB is a plain [Effects.AddCounters]. The Renew ability adds one +1/+1 counter and then
 * doubles the resulting total via [Effects.DoubleCounters] (reads the post-add count, so the
 * creature ends with 2×(existing+1) counters). Both target the same creature; the doubling
 * runs on the same [Targets.Creature] reference, so "that creature" resolves to the one that
 * received the new counter.
 */
val SageOfTheFang = card("Sage of the Fang") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Human Druid"
    power = 2
    toughness = 2
    oracleText = "When this creature enters, put a +1/+1 counter on target creature.\n" +
        "Renew — {3}{G}, Exile this card from your graveyard: Put a +1/+1 counter on target " +
        "creature, then double the number of +1/+1 counters on that creature. Activate only as " +
        "a sorcery."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val creature = target("creature", Targets.Creature)
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, creature)
        description = "When this creature enters, put a +1/+1 counter on target creature."
    }

    renew("{3}{G}") {
        val creature = target("creature", Targets.Creature)
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, creature)
            .then(Effects.DoubleCounters(Counters.PLUS_ONE_PLUS_ONE, creature))
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "155"
        artist = "Ioannis Fiore"
        imageUri = "https://cards.scryfall.io/normal/front/1/e/1ebf4a9d-d90c-4017-9f00-fca89899f301.jpg?1743403231"
        ruling("2025-04-04", "If a card with a renew ability is put into your graveyard during your turn, you can activate that ability if it's legal to do so before any other player can take any actions.")
    }
}
