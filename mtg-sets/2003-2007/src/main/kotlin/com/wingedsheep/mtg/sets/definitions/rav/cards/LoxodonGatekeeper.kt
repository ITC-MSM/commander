package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EventPattern
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.PermanentsEnterTapped

/**
 * Loxodon Gatekeeper
 * {2}{W}{W}
 * Creature — Elephant Soldier
 * 2/3
 *
 * Artifacts, creatures, and lands your opponents control enter tapped.
 *
 * Three separate [PermanentsEnterTapped] replacements rather than one union filter, following
 * Thalia, Heretic Cathar: "artifact", "creature" and "land" are independent characteristic checks,
 * so an artifact creature (or a land that has become a creature) matches more than one and simply
 * enters tapped once. A union filter would need an `AnyOf` that says nothing extra here.
 *
 * `appliesTo` describes the *affected* permanent, not the Gatekeeper — this is the group
 * counterpart of the self-only `EntersTapped`, consulted from the battlefield whenever some other
 * permanent enters. The controller-relative `opponentControls()` predicate resolves against the
 * Gatekeeper's own controller at entry time, so permanents entering *simultaneously* with the
 * Gatekeeper are unaffected (the replacement does not exist until it is on the battlefield).
 *
 * Note the noun list stops at three types: enchantments and planeswalkers your opponents control
 * are untouched, which is the whole reason this is not `GameObjectFilter.Permanent`.
 */
val LoxodonGatekeeper = card("Loxodon Gatekeeper") {
    manaCost = "{2}{W}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Elephant Soldier"
    oracleText = "Artifacts, creatures, and lands your opponents control enter tapped."
    power = 2
    toughness = 3

    // Artifacts your opponents control enter tapped.
    replacementEffect(
        PermanentsEnterTapped(
            appliesTo = EventPattern.ZoneChangeEvent(
                filter = GameObjectFilter.Artifact.opponentControls(),
                to = Zone.BATTLEFIELD,
            )
        )
    )

    // Creatures your opponents control enter tapped.
    replacementEffect(
        PermanentsEnterTapped(
            appliesTo = EventPattern.ZoneChangeEvent(
                filter = GameObjectFilter.Creature.opponentControls(),
                to = Zone.BATTLEFIELD,
            )
        )
    )

    // Lands your opponents control enter tapped.
    replacementEffect(
        PermanentsEnterTapped(
            appliesTo = EventPattern.ZoneChangeEvent(
                filter = GameObjectFilter.Land.opponentControls(),
                to = Zone.BATTLEFIELD,
            )
        )
    )

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "25"
        artist = "Carl Critchlow"
        flavorText = "The gatekeepers are so fastidious that even the winds must wait to pass."
        imageUri = "https://cards.scryfall.io/normal/front/7/8/781476a1-73a9-4f43-9f71-d29c97ecc69a.jpg?1783943696"
    }
}
