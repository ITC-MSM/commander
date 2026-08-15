package com.wingedsheep.mtg.sets.definitions.blb.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.conditions.Exists
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect
import com.wingedsheep.sdk.scripting.references.Player

/**
 * Dazzling Denial {1}{U}
 * Instant
 *
 * Counter target spell unless its controller pays {2}. If you control a Bird,
 * counter that spell unless its controller pays {4} instead.
 */
val DazzlingDenial = card("Dazzling Denial") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Instant"
    oracleText = "Counter target spell unless its controller pays {2}. If you control a Bird, counter that spell unless its controller pays {4} instead."

    spell {
        target("target spell", Targets.Spell)
        effect = ConditionalEffect(
            condition = Exists(Player.You, Zone.BATTLEFIELD, GameObjectFilter.Creature.withSubtype("Bird")),
            effect = Effects.CounterUnlessPays("{4}"),
            elseEffect = Effects.CounterUnlessPays("{2}")
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "45"
        artist = "Kisung Koh"
        flavorText = "Maintaining brilliant plumage isn't just a statement. It's a tactic."
        imageUri = "https://cards.scryfall.io/normal/front/8/7/8739f1ac-2e57-4b52-a7ff-cc8df5936aad.jpg?1721426047"
    }
}
