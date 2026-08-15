package com.wingedsheep.mtg.sets.definitions.dst.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EventPattern
import com.wingedsheep.sdk.scripting.RedirectZoneChange

/**
 * Darksteel Colossus — Darksteel #109 (canonical printing; reprinted in Foundations #671).
 * {11} · Artifact Creature — Golem · 11/11
 *
 * Trample, indestructible.
 * If Darksteel Colossus would be put into a graveyard from anywhere, reveal it and shuffle it
 * into its owner's library instead.
 *
 * The shuffle-back is a card-intrinsic zone-change replacement (`selfOnly = true`), so it functions
 * in every zone (CR 614.12): the Colossus shuffles back not only when it dies, but also when it is
 * milled from the library, discarded, or countered on the stack. It is carried on the card entity
 * via [com.wingedsheep.engine.state.components.identity.SelfZoneRedirectComponent] rather than being
 * a battlefield-only static, and [shuffleIntoLibrary] shuffles it in rather than placing it on top.
 * `reveal` is informational — a public-zone card is already known — so it drives the flavor text of
 * the redirect, not a separate game action.
 */
val DarksteelColossus = card("Darksteel Colossus") {
    manaCost = "{11}"
    colorIdentity = ""
    typeLine = "Artifact Creature — Golem"
    oracleText = "Trample, indestructible\n" +
        "If Darksteel Colossus would be put into a graveyard from anywhere, reveal Darksteel " +
        "Colossus and shuffle it into its owner's library instead."
    power = 11
    toughness = 11
    keywords(Keyword.TRAMPLE, Keyword.INDESTRUCTIBLE)

    replacementEffect(
        RedirectZoneChange(
            newDestination = Zone.LIBRARY,
            appliesTo = EventPattern.ZoneChangeEvent(to = Zone.GRAVEYARD),
            selfOnly = true,
            shuffleIntoLibrary = true,
            reveal = true,
        )
    )

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "109"
        artist = "Carl Critchlow"
        imageUri = "https://cards.scryfall.io/normal/front/c/b/cbc27b24-f085-48b0-8757-cd11fbf25b91.jpg?1783944427"
    }
}
