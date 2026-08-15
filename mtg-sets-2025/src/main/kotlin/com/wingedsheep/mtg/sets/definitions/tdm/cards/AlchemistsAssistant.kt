package com.wingedsheep.mtg.sets.definitions.tdm.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.renew
import com.wingedsheep.sdk.model.Rarity

/**
 * Alchemist's Assistant — Tarkir: Dragonstorm #71
 * {1}{B} · Creature — Monkey · 2/1
 *
 * Lifelink
 * Renew — {1}{B}, Exile this card from your graveyard: Put a lifelink counter on target
 * creature. Activate only as a sorcery.
 *
 * Lifelink is a keyword counter (CR 122.1d): the [StateProjector]'s KEYWORD_COUNTER_MAP grants
 * the keyword to any creature carrying a lifelink counter, so the Renew ability is a single
 * [Effects.AddCounters] on one target.
 */
val AlchemistsAssistant = card("Alchemist's Assistant") {
    manaCost = "{1}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Monkey"
    power = 2
    toughness = 1
    oracleText = "Lifelink\n" +
        "Renew — {1}{B}, Exile this card from your graveyard: Put a lifelink counter on target " +
        "creature. Activate only as a sorcery."

    keywords(Keyword.LIFELINK)

    renew("{1}{B}") {
        effect = Effects.AddCounters(Counters.LIFELINK, 1, target("creature", Targets.Creature))
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "71"
        artist = "Eelis Kyttanen"
        imageUri = "https://cards.scryfall.io/normal/front/4/d/4d305609-64f8-4f3f-bf67-cd5257f0d01e.jpg?1743204244"
    }
}
