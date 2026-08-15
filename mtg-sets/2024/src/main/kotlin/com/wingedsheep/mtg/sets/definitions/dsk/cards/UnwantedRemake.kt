package com.wingedsheep.mtg.sets.definitions.dsk.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Unwanted Remake
 * {W}
 * Instant
 * Destroy target creature. Its controller manifests dread. (That player looks at the top two cards
 * of their library, then puts one onto the battlefield face down as a 2/2 creature and the other
 * into their graveyard. If it's a creature card, it can be turned face up any time for its mana
 * cost.)
 *
 * Modeled with existing primitives — the [FearOfImpostors] "its controller manifests dread" shape:
 * - [Effects.Destroy] destroys the targeted creature.
 * - "Its controller manifests dread" runs the shared [Patterns.Library.manifestDread] recipe under
 *   the destroyed creature's controller via [Effects.ForEachPlayer] over the single
 *   [Player.ControllerOf] the target. The iteration rebinds the body's controller to that player, so
 *   they look at their own library, choose, and the face-down 2/2 enters under their control.
 *   `Player.ControllerOf` resolves from the chosen target even after the destroy, since target ids
 *   are captured at resolution.
 * - Per the Scryfall ruling, an illegal target on resolution fizzles the whole spell (no manifest
 *   dread) — the standard targeting behavior, no special casing needed.
 */
val UnwantedRemake = card("Unwanted Remake") {
    manaCost = "{W}"
    colorIdentity = "W"
    typeLine = "Instant"
    oracleText = "Destroy target creature. Its controller manifests dread. (That player looks at the " +
        "top two cards of their library, then puts one onto the battlefield face down as a 2/2 " +
        "creature and the other into their graveyard. If it's a creature card, it can be turned face " +
        "up any time for its mana cost.)"

    spell {
        target("target creature", Targets.Creature)
        effect = Effects.Composite(
            Effects.Destroy(EffectTarget.ContextTarget(0)),
            // Its controller manifests dread — run the shared recipe under the target's controller.
            Effects.ForEachPlayer(
                players = Player.ControllerOf("target creature"),
                effects = Patterns.Library.manifestDread().effects,
            ),
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "39"
        artist = "Eli Minaya"
        imageUri = "https://cards.scryfall.io/normal/front/7/b/7b54447f-1daf-4352-b5c3-3c0ec8b8f4d0.jpg?1726286001"
        ruling(
            "2024-09-20",
            "If the target creature is an illegal target as Unwanted Remake tries to resolve, it " +
                "won't resolve and none of its effects will happen. The creature's controller won't manifest dread."
        )
    }
}
