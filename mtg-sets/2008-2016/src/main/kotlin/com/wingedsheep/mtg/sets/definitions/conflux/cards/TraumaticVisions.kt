package com.wingedsheep.mtg.sets.definitions.conflux.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Traumatic Visions
 * {3}{U}{U}
 * Instant
 * Counter target spell.
 * Basic landcycling {1}{U}
 *
 * A plain [Effects.CounterSpell] over [Targets.Spell] — an unfiltered `TargetObject` scoped to the
 * stack, which is what "target spell" (no type restriction) means.
 *
 * "Basic landcycling" is [KeywordAbility.basicLandcycling], the typecycling machinery narrowed to
 * *basic* land cards, matching the `IsBasicLand` search filter in the reminder text.
 */
val TraumaticVisions = card("Traumatic Visions") {
    manaCost = "{3}{U}{U}"
    colorIdentity = "U"
    typeLine = "Instant"
    oracleText = "Counter target spell.\n" +
        "Basic landcycling {1}{U} ({1}{U}, Discard this card: Search your library for a basic land " +
        "card, reveal it, put it into your hand, then shuffle.)"

    spell {
        target("target", Targets.Spell)
        effect = Effects.CounterSpell()
    }

    keywordAbility(KeywordAbility.basicLandcycling("{1}{U}"))

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "36"
        artist = "Cyril Van Der Haegen"
        imageUri = "https://cards.scryfall.io/normal/front/f/1/f1e8b03d-9265-4699-b626-5efa73292d43.jpg"
    }
}
