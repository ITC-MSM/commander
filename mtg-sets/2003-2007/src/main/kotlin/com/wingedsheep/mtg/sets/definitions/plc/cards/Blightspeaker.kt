package com.wingedsheep.mtg.sets.definitions.plc.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.SearchDestination

/**
 * Blightspeaker
 * {1}{B}
 * Creature — Human Rebel Cleric
 * 1/1
 * {T}: Target player loses 1 life.
 * {4}, {T}: Search your library for a Rebel permanent card with mana value 3 or less, put it onto the battlefield, then shuffle.
 *
 * A "Rebel **permanent** card" is any permanent card with the subtype, not only a creature one —
 * the Rebel search chain reaches Rebel-typed noncreature permanents too.
 */
val Blightspeaker = card("Blightspeaker") {
    manaCost = "{1}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Human Rebel Cleric"
    power = 1
    toughness = 1
    oracleText = "{T}: Target player loses 1 life.\n" +
        "{4}, {T}: Search your library for a Rebel permanent card with mana value 3 or less, put it onto the battlefield, then shuffle."

    activatedAbility {
        cost = Costs.Tap
        val p = target("target", Targets.Player)
        effect = Effects.LoseLife(1, p)
        description = "{T}: Target player loses 1 life."
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{4}"), Costs.Tap)
        effect = Patterns.Library.searchLibrary(
            filter = GameObjectFilter.Permanent.withSubtype(Subtype.REBEL).manaValueAtMost(3),
            destination = SearchDestination.BATTLEFIELD
        )
        description = "{4}, {T}: Search your library for a Rebel permanent card with mana value 3 or less, put it onto the battlefield, then shuffle."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "64"
        artist = "Ron Spears"
        flavorText = "One croaked sermon spreads propaganda and plague."
        imageUri = "https://cards.scryfall.io/normal/front/4/a/4ae24bc0-1217-4db9-a745-8860f23b6d57.jpg"
    }
}
