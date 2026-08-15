package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetPermanent

/**
 * Ice Flan
 * {4}{U}{U}
 * Creature — Elemental Ooze
 * 5/4
 * When this creature enters, tap target artifact or creature an opponent controls.
 * Put a stun counter on it.
 * Islandcycling {2}
 */
val IceFlan = card("Ice Flan") {
    manaCost = "{4}{U}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Elemental Ooze"
    power = 5
    toughness = 4
    oracleText = "When this creature enters, tap target artifact or creature an opponent controls. " +
        "Put a stun counter on it. (If a permanent with a stun counter would become untapped, " +
        "remove one from it instead.)\n" +
        "Islandcycling {2} ({2}, Discard this card: Search your library for an Island card, " +
        "reveal it, put it into your hand, then shuffle.)"

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val t = target(
            "target",
            TargetPermanent(filter = TargetFilter(GameObjectFilter.CreatureOrArtifact.opponentControls()))
        )
        effect = Effects.Composite(
            Effects.Tap(t),
            Effects.AddCounters(Counters.STUN, 1, t),
        )
    }

    keywordAbility(KeywordAbility.typecycling("Island", ManaCost.parse("{2}")))

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "55"
        artist = "SHOSUKE"
        imageUri = "https://cards.scryfall.io/normal/front/a/d/ad304c9c-943f-442f-bb82-ff378ad7d7ba.jpg?1748705958"
    }
}
