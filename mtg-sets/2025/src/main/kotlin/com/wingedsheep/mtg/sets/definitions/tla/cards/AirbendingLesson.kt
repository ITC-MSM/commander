package com.wingedsheep.mtg.sets.definitions.tla.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Airbending Lesson — {2}{W} Instant — Lesson
 *
 * Airbend target nonland permanent. (Exile it. While it's exiled, its owner may cast
 * it for {2} rather than its mana cost.)
 * Draw a card.
 *
 * Airbend is a keyword *action*, not a keyword ability — there is no `keywordAbility(...)`.
 * [Effects.Airbend] is target-agnostic: it airbends the spell's chosen target(s) via
 * `CardSource.ChosenTargets`, so the `target("target nonland permanent", …)` declaration
 * supplies the target and the effect needs no explicit argument.
 */
val AirbendingLesson = card("Airbending Lesson") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Instant — Lesson"
    oracleText = "Airbend target nonland permanent. (Exile it. While it's exiled, its owner may cast it for {2} rather than its mana cost.)\nDraw a card."

    spell {
        target("target nonland permanent", Targets.NonlandPermanent)
        effect = Effects.Composite(
            Effects.Airbend(),
            Effects.DrawCards(1)
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "8"
        artist = "Pisukev"
        flavorText = "\"Very interesting move, young one.\"\n—Monk Gyatso"
        imageUri = "https://cards.scryfall.io/normal/front/e/2/e2bcd0a6-e94d-4a21-a334-a57459c1b8cc.jpg?1764119921"
    }
}
