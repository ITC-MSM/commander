package com.wingedsheep.mtg.sets.definitions.tdm.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.renew
import com.wingedsheep.sdk.model.Rarity

/**
 * Champion of Dusan
 * {2}{G}
 * Creature — Human Warrior
 * 4/2
 *
 * Trample
 * Renew — {1}{G}, Exile this card from your graveyard: Put a +1/+1 counter and a
 *   trample counter on target creature. Activate only as a sorcery.
 *
 * The renew payoff puts both counter kinds on a single target via two chained
 * [Effects.AddCounters] calls (same pattern as Kheru Goldkeeper); the trample counter
 * is a keyword counter (CR 122.1c) granting trample through the layer system.
 */
val ChampionOfDusan = card("Champion of Dusan") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Human Warrior"
    power = 4
    toughness = 2
    oracleText = "Trample\n" +
        "Renew — {1}{G}, Exile this card from your graveyard: Put a +1/+1 counter and a " +
        "trample counter on target creature. Activate only as a sorcery."

    keywords(Keyword.TRAMPLE)

    renew("{1}{G}") {
        val creature = target("creature", Targets.Creature)
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, creature)
            .then(Effects.AddCounters(Counters.TRAMPLE, 1, creature))
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "137"
        artist = "Bastien L. Deharme"
        flavorText = "Dusan boxing matches allow contestants no weapons, no allies, and no excuses."
        imageUri = "https://cards.scryfall.io/normal/front/c/5/c51dcdab-38ee-4804-8859-09adc353c182.jpg?1743204513"
        ruling("2025-04-04", "If a card with a renew ability is put into your graveyard during your turn, you can activate that ability if it's legal to do so before any other player can take any actions.")
    }
}
