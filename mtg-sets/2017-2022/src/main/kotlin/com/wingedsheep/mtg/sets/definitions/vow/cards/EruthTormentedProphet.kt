package com.wingedsheep.mtg.sets.definitions.vow.cards

import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ReplaceDrawWithEffect

/**
 * Eruth, Tormented Prophet (Innistrad: Crimson Vow #237)
 * {1}{U}{R} · Legendary Creature — Human Wizard 2/4
 *
 * If you would draw a card, exile the top two cards of your library instead. You may play those
 * cards this turn.
 *
 * Implementation: [ReplaceDrawWithEffect] (the Laboratory Maniac shape, applying to every draw)
 * whose replacement is the impulse-draw composition [Patterns.Exile.impulse] at count 2 — exile
 * the top two, then a may-play-from-exile grant on those cards that expires at end of turn. Each
 * replaced draw is its own impulse, so "draw two" exiles four. An empty library exiles nothing and
 * the draw was still replaced, so it can't be lost to (the card's own ruling).
 */
val EruthTormentedProphet = card("Eruth, Tormented Prophet") {
    manaCost = "{1}{U}{R}"
    colorIdentity = "UR"
    typeLine = "Legendary Creature — Human Wizard"
    power = 2
    toughness = 4
    oracleText = "If you would draw a card, exile the top two cards of your library instead. You may " +
        "play those cards this turn."

    replacementEffect(
        ReplaceDrawWithEffect(
            replacementEffect = Patterns.Exile.impulse(2),
        )
    )

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "237"
        artist = "Ekaterina Burmak"
        flavorText = "She is cursed with visions of monsters and suffering . . . and all her visions come true."
        imageUri = "https://cards.scryfall.io/normal/front/9/f/9f764077-df2d-4ac7-b507-2c8e08386d49.jpg?1783924793"
        ruling(
            "2021-11-19",
            "You may choose to apply Eruth's replacement effect even if there are fewer than two cards " +
                "in your library. If there are no cards in your library, you won't exile any cards this " +
                "way, but you also won't lose the game for having attempted to draw from an empty library."
        )
    }
}
