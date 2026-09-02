package com.wingedsheep.mtg.sets.definitions.stx.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Expanded Anatomy — Strixhaven: School of Mages #2 (canonical printing)
 * {3} · Sorcery — Lesson
 *
 * Put two +1/+1 counters on target creature. It gains vigilance until end of turn.
 *
 * One target, two effects in printed order: [Effects.AddCounters] for the two +1/+1 counters, then
 * [Effects.GrantKeyword] for the until-end-of-turn vigilance, both bound to the same creature.
 * Lesson is only a subtype.
 */
val ExpandedAnatomy = card("Expanded Anatomy") {
    manaCost = "{3}"
    colorIdentity = ""
    typeLine = "Sorcery — Lesson"
    oracleText =
        "Put two +1/+1 counters on target creature. It gains vigilance until end of turn."

    spell {
        val creature = target("target creature", Targets.Creature)
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 2, creature) then
            Effects.GrantKeyword(Keyword.VIGILANCE, creature)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "2"
        artist = "Slawomir Maniak"
        flavorText = "\"Changing the equation from incremental to exponential was a stroke of genius on my part.\""
        imageUri = "https://cards.scryfall.io/normal/front/c/5/c5642b9d-0daa-4e6b-ad48-f88dd37d6574.jpg?1783927396"
    }
}
