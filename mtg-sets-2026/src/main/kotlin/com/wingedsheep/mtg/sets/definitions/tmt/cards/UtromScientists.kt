package com.wingedsheep.mtg.sets.definitions.tmt.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Utrom Scientists
 * {2}{U}
 * Artifact Creature — Utrom Robot Scientist
 * 2/2
 *
 * When this creature enters, tap up to one target creature and put a
 * stun counter on it.
 */
val UtromScientists = card("Utrom Scientists") {
    manaCost = "{2}{U}"
    colorIdentity = "U"
    typeLine = "Artifact Creature — Utrom Robot Scientist"
    oracleText = "When this creature enters, tap up to one target creature and put a stun counter on it. (If a permanent with a stun counter would become untapped, remove one from it instead.)"
    power = 2
    toughness = 2

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val creature = target(
            "up to one target creature",
            TargetCreature(count = 1, optional = true)
        )
        effect = Effects.Tap(creature)
            .then(Effects.AddCounters(Counters.STUN, 1, creature))
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "56"
        artist = "Miklós Ligeti"
        flavorText = "Using exoskeletons and false skins, the utroms took up residence on Earth after their ship crashed. Returning home using only Earth's primitive technology has proven challenging."
        imageUri = "https://cards.scryfall.io/normal/front/6/d/6da89625-5278-49d4-813b-a1a631f114f5.jpg?1771502610"
    }
}
