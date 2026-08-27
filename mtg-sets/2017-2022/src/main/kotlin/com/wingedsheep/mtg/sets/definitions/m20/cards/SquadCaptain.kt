package com.wingedsheep.mtg.sets.definitions.m20.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersWithDynamicCounters
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.values.DynamicAmount


/**
 * Squad Captain
 * {4}{W}
 * Creature — Human Soldier
 * 2/2
 * Vigilance (Attacking doesn't cause this creature to tap.)
 * This creature enters with a +1/+1 counter on it for each other creature you control.
 *
 * "Other" is `AggregateBattlefield.excludeSelf` — a field on the tally, not a filter predicate —
 * so the Captain never counts itself as it enters.
 *
 * This card was a `mtgish-tooling` render that nobody had reviewed, and it counted
 * `Creature.attacking()` with no `excludeSelf`: on a main-phase cast nothing is attacking, so it
 * entered as a plain 2/2 every time. Argentum Assay's differential found it on the day the
 * counters-per-count rule made the line comparable.
 */
val SquadCaptain = card("Squad Captain") {
    manaCost = "{4}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Soldier"
    oracleText = "Vigilance (Attacking doesn't cause this creature to tap.)\nThis creature enters with a +1/+1 counter on it for each other creature you control."
    power = 2
    toughness = 2
    keywords(Keyword.VIGILANCE)
    replacementEffect(
        EntersWithDynamicCounters(
            count = DynamicAmount.AggregateBattlefield(
                player = Player.You,
                filter = GameObjectFilter.Creature,
                excludeSelf = true,
            )
        )
    )
    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "38"
        artist = "Cristi Balanescu"
        flavorText = "The strength of the one is the strength of the many."
        imageUri = "https://cards.scryfall.io/normal/front/b/d/bd7d6112-ffb2-41d8-98ed-1a7b22841dfd.jpg"
    }
}
