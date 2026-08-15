package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.effects.AddCountersEffect
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Cloudbound Moogle
 * {3}{W}{W}
 * Creature — Moogle
 * 2/3
 * Flying
 * When this creature enters, put a +1/+1 counter on target creature.
 * Plainscycling {2}
 */
val CloudboundMoogle = card("Cloudbound Moogle") {
    manaCost = "{3}{W}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Moogle"
    power = 2
    toughness = 3
    oracleText = "Flying\n" +
        "When this creature enters, put a +1/+1 counter on target creature.\n" +
        "Plainscycling {2} ({2}, Discard this card: Search your library for a Plains card, " +
        "reveal it, put it into your hand, then shuffle.)"

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val t = target("creature", TargetCreature(filter = TargetFilter.Creature))
        effect = AddCountersEffect(Counters.PLUS_ONE_PLUS_ONE, 1, t)
    }

    keywordAbility(KeywordAbility.typecycling("Plains", ManaCost.parse("{2}")))

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "11"
        artist = "Andrea Radeck"
        imageUri = "https://cards.scryfall.io/normal/front/7/3/7387bca7-f496-45da-a0ac-6be049303a8f.jpg?1748705792"
    }
}
