package com.wingedsheep.mtg.sets.definitions.vow.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetPermanent

/**
 * Aim for the Head
 * {2}{B}
 * Sorcery
 *
 * Choose one —
 * • Exile target Zombie.
 * • Target opponent exiles two cards from their hand.
 *
 * Mode 1 is a plain [Effects.Exile] over a subtype-scoped target. The noun is **`Permanent`, not
 * `Creature`**: a bare tribal noun in Oracle names permanents of that type, so an artifact Zombie
 * or an enchantment Zombie is a legal target for it. Mode 2 is [Patterns.Hand.exileFromHand], whose default target is the mode's own
 * `ContextTarget(0)`: it gathers the targeted opponent's hand, has *that player* pick two of
 * their own cards, and moves the selection to exile. The printed ruling — "if the target
 * opponent only has one card in hand, they will exile that card" — falls out of the recipe's
 * `SelectFromCollection`, which takes everything eligible rather than stalling when the
 * collection is smaller than the requested count.
 */
val AimForTheHead = card("Aim for the Head") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Sorcery"
    oracleText = "Choose one —\n" +
        "• Exile target Zombie.\n" +
        "• Target opponent exiles two cards from their hand."

    spell {
        modal(chooseCount = 1) {
            mode("Exile target Zombie") {
                val zombie = target(
                    "target Zombie",
                    TargetPermanent(filter = TargetFilter(GameObjectFilter.Permanent.withSubtype("Zombie")))
                )
                effect = Effects.Exile(zombie)
            }
            mode("Target opponent exiles two cards from their hand") {
                target("opponent", Targets.Opponent)
                effect = Patterns.Hand.exileFromHand(2)
            }
        }
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "92"
        artist = "Zoltan Boros"
        flavorText = "\"Works every time.\"\n—Halana, Kessig ranger"
        imageUri = "https://cards.scryfall.io/normal/front/1/1/1174e8e1-2e8e-4070-9871-7d5d93e0dd56.jpg?1783924875"
        ruling("2021-11-19", "If the target opponent only has one card in hand, they will exile that card.")
    }
}
