package com.wingedsheep.mtg.sets.definitions.dsk.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.CreateDelayedTriggerEffect
import com.wingedsheep.sdk.scripting.effects.DelayedTriggerExpiry
import com.wingedsheep.sdk.scripting.effects.ModifyStatsEffect

/**
 * Turn Inside Out
 * {R}
 * Instant
 * Target creature gets +3/+0 until end of turn. When it dies this turn, manifest dread. (Look at
 * the top two cards of your library. Put one onto the battlefield face down as a 2/2 creature and
 * the other into your graveyard. Turn it face up any time for its mana cost if it's a creature
 * card.)
 *
 * Modeled like Desperate Measures: a +3/+0 stat change until end of turn plus a watched-entity
 * delayed [Triggers.Dies] trigger scoped to the buffed creature via `watchedTarget`, expiring at
 * end of turn. When the creature dies this turn, the delayed trigger runs the shared
 * [Patterns.Library.manifestDread] recipe for the spell's controller. The trigger is scoped by
 * entity id, so it fires regardless of who controlled the creature when it died — matching the
 * printed "When it dies this turn" (no controller restriction).
 */
val TurnInsideOut = card("Turn Inside Out") {
    manaCost = "{R}"
    colorIdentity = "R"
    typeLine = "Instant"
    oracleText = "Target creature gets +3/+0 until end of turn. When it dies this turn, manifest " +
        "dread. (Look at the top two cards of your library. Put one onto the battlefield face down " +
        "as a 2/2 creature and the other into your graveyard. Turn it face up any time for its " +
        "mana cost if it's a creature card.)"

    spell {
        val t = target("target creature", Targets.Creature)
        effect = Effects.Composite(
            ModifyStatsEffect(3, 0, t),
            CreateDelayedTriggerEffect(
                effect = Patterns.Library.manifestDread(),
                trigger = Triggers.Dies,
                watchedTarget = t,
                expiry = DelayedTriggerExpiry.EndOfTurn,
            ),
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "160"
        artist = "Loïc Canavaggia"
        flavorText = "\"Scared, are you? Need a hug?\""
        imageUri = "https://cards.scryfall.io/normal/front/5/7/57e2a92c-06d3-4cb5-883d-cba428a7e98e.jpg?1726286448"
    }
}
