package com.wingedsheep.mtg.sets.definitions.war.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetPermanent

/**
 * Gideon's Company — War of the Spark #268 (canonical printing)
 * {3}{W}
 * Creature — Human Soldier
 * 3/3
 * Whenever you gain life, put two +1/+1 counters on this creature.
 * {3}{W}: Put a loyalty counter on target Gideon planeswalker.
 *
 * A planeswalker-deck exclusive, so `inBooster = false` — it is part of the set's card pool but
 * never appears in a booster.
 *
 * The activated ability adds a [Counters.LOYALTY] counter like any other counter type; loyalty is
 * not special-cased outside the planeswalker's own cost and damage rules. "Target Gideon
 * planeswalker" is the planeswalker filter narrowed by the *subtype*, not by name — it would
 * find any Gideon on the battlefield, yours or an opponent's.
 */
val GideonsCompany = card("Gideon's Company") {
    manaCost = "{3}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Soldier"
    oracleText = "Whenever you gain life, put two +1/+1 counters on this creature.\n" +
        "{3}{W}: Put a loyalty counter on target Gideon planeswalker."
    power = 3
    toughness = 3

    triggeredAbility {
        trigger = Triggers.YouGainLife
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 2, EffectTarget.Self)
    }

    activatedAbility {
        cost = Costs.Mana("{3}{W}")
        val gideon = target("target", TargetPermanent(filter = TargetFilter.Planeswalker.withSubtype("Gideon")))
        effect = Effects.AddCounters(Counters.LOYALTY, 1, gideon)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "268"
        artist = "Milivoj Ćeran"
        flavorText = "\"For the Legion! For Ravnica!\""
        imageUri = "https://cards.scryfall.io/normal/front/a/0/a0a64d2e-8b89-4642-84c2-b01dfe11312e.jpg"
        inBooster = false
    }
}
