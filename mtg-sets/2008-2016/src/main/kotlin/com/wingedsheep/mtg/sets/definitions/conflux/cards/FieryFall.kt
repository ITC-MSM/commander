package com.wingedsheep.mtg.sets.definitions.conflux.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Fiery Fall
 * {5}{R}
 * Instant
 * Fiery Fall deals 5 damage to target creature.
 * Basic landcycling {1}{R} ({1}{R}, Discard this card: Search your library for a basic land card,
 * reveal it, put it into your hand, then shuffle.)
 *
 * The spell half is a plain [Effects.DealDamage] over one named target ([Targets.Creature]).
 * Basic landcycling is [KeywordAbility.basicLandcycling], which is the shared cycling machinery
 * with its search narrowed to *basic* land cards and its reminder text prefixed "Basic
 * landcycling" — it carries its own cost, so no `keywords(...)` entry belongs alongside it.
 */
val FieryFall = card("Fiery Fall") {
    manaCost = "{5}{R}"
    colorIdentity = "R"
    typeLine = "Instant"
    oracleText = "Fiery Fall deals 5 damage to target creature.\n" +
        "Basic landcycling {1}{R} ({1}{R}, Discard this card: Search your library for a basic land " +
        "card, reveal it, put it into your hand, then shuffle.)"

    spell {
        val t = target("target", Targets.Creature)
        effect = Effects.DealDamage(5, t)
    }

    keywordAbility(KeywordAbility.basicLandcycling("{1}{R}"))

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "63"
        artist = "Daarken"
        flavorText = "Jund feasts on the unprepared."
        imageUri = "https://cards.scryfall.io/normal/front/6/8/687bb467-b447-4901-8a65-cd91fd3aa15d.jpg"
    }
}
