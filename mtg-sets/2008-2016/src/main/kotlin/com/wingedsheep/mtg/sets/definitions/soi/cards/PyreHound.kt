package com.wingedsheep.mtg.sets.definitions.soi.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Pyre Hound (Shadows over Innistrad #174)
 * {3}{R}
 * Creature — Elemental Dog
 * 2 / 3
 *
 * Trample
 * Whenever you cast an instant or sorcery spell, put a +1/+1 counter on this creature.
 *
 * The counters are permanent — this is the counter-stacking cousin of prowess, so
 * [Triggers.YouCastInstantOrSorcery] plus a plain [Effects.AddCounters] on the source.
 */
val PyreHound = card("Pyre Hound") {
    manaCost = "{3}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Elemental Dog"
    power = 2
    toughness = 3
    oracleText = "Trample\n" +
        "Whenever you cast an instant or sorcery spell, put a +1/+1 counter on this creature."

    keywords(Keyword.TRAMPLE)

    triggeredAbility {
        trigger = Triggers.YouCastInstantOrSorcery
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "174"
        artist = "Jama Jurabaev"
        flavorText = "Its growl is the crackling and popping of a wildfire untamed."
        imageUri = "https://cards.scryfall.io/normal/front/5/d/5d19c5fb-01af-4ca4-af7e-00ec2a748afd.jpg?1783937746"
    }
}
