package com.wingedsheep.mtg.sets.definitions.dft.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.startYourEngines
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EventPattern
import com.wingedsheep.sdk.scripting.ModifyDrawAmount
import com.wingedsheep.sdk.scripting.NoMaximumHandSize
import com.wingedsheep.sdk.scripting.references.Player

/**
 * Vnwxt, Verbose Host
 * {1}{U}
 * Legendary Creature — Homunculus
 * 0/4
 *
 * Start your engines!
 * You have no maximum hand size.
 * Max speed — If you would draw a card, draw two cards instead.
 *
 * The max-speed clause is a *replacement* effect, so it can't go through the `maxSpeed { }`
 * block — that gates static/activated/triggered abilities, and replacement effects are read
 * straight off `ReplacementEffectSourceComponent` at interception sites that don't evaluate a
 * gate. [ModifyDrawAmount] carries its own `restrictions` slot, evaluated in the drawing
 * player's context at the one announcement site (CR 121.2a), so the gate folds into the
 * effect itself exactly the way `maxSpeed { }` folds it into `ModifySpellCost.gating`. The
 * display-only [Keyword.MAX_SPEED] badge is added by hand for the same reason.
 *
 * `multiplier = 2` rather than `modifier = 1`: the rulings are explicit that this multiplies
 * the announced count, so Harmonize draws six, and two such effects quadruple it.
 */
val VnwxtVerboseHost = card("Vnwxt, Verbose Host") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Legendary Creature — Homunculus"
    power = 0
    toughness = 4
    oracleText = "Start your engines! (If you have no speed, it starts at 1. It increases once on " +
        "each of your turns when an opponent loses life. Max speed is 4.)\n" +
        "You have no maximum hand size.\n" +
        "Max speed — If you would draw a card, draw two cards instead."

    startYourEngines()
    keywords(Keyword.MAX_SPEED)

    staticAbility {
        ability = NoMaximumHandSize
    }

    replacementEffect(
        ModifyDrawAmount(
            multiplier = 2,
            restrictions = listOf(Conditions.YouHaveMaxSpeed),
            appliesTo = EventPattern.DrawEvent(player = Player.You),
        )
    )

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "73"
        artist = "Izzy"
        flavorText = "\"Racers! Start! Your! Engiiiiiines!\""
        imageUri = "https://cards.scryfall.io/normal/front/8/9/893254c7-64cc-4cb9-b79f-2c41a8935ea0.jpg?1783907900"

        ruling(
            "2025-02-07",
            "If a spell or ability causes you to draw multiple cards, this creature's last ability " +
                "doubles each card draw. For example, if you cast Harmonize (\"Draw three cards\"), " +
                "you'll draw six cards."
        )
        ruling(
            "2025-02-07",
            "The effects of multiple such effects are cumulative. For example, if you have max speed " +
                "and control both Vnwxt and Thought Reflection (an enchantment with the same ability), " +
                "you'll draw four times the original number of cards."
        )
        ruling(
            "2025-02-07",
            "If two or more replacement effects would apply to a card-drawing event, the player who's " +
                "drawing the card chooses what order to apply them."
        )
    }
}
