package com.wingedsheep.mtg.sets.definitions.dsk.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.effects.FaceDownMode
import com.wingedsheep.sdk.scripting.effects.GatherCardsEffect
import com.wingedsheep.sdk.scripting.effects.LookAudience
import com.wingedsheep.sdk.scripting.effects.MoveCollectionEffect
import com.wingedsheep.sdk.scripting.effects.ForEachPlayerEffect
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Doomsday Excruciator
 * {B}{B}{B}{B}{B}{B}
 * Creature — Demon
 * 6/6
 * Flying
 * When this creature enters, if it was cast, each player exiles all but the bottom six cards of
 * their library face down.
 * At the beginning of your upkeep, draw a card.
 *
 * Modeled with existing primitives:
 * - Flying via the keyword.
 * - The ETB is a [Triggers.EntersBattlefield] gated by [Conditions.WasCast] (the intervening "if it
 *   was cast" clause — it does nothing for a token copy or a creature put onto the battlefield
 *   without being cast). "Each player exiles all but the bottom six cards of their library face down"
 *   is one [Effects.ForEachPlayer] over [Player.Each]; the iteration rebinds the body's controller to
 *   each player so [CardSource.TopOfLibrary] (defaulting to `Player.You`) reads that player's own
 *   library. We gather the top `librarySize - 6` cards — i.e. everything but the bottom six —
 *   clamped to zero with [DynamicAmount.IfPositive] so a player with six or fewer cards exiles
 *   nothing (matching the Scryfall ruling). Those cards move to exile face down
 *   ([FaceDownMode.HIDDEN], hidden in exile) via [MoveCollectionEffect].
 * - The upkeep payoff is a standard [Triggers.YourUpkeep] drawing a card.
 */
val DoomsdayExcruciator = card("Doomsday Excruciator") {
    manaCost = "{B}{B}{B}{B}{B}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Demon"
    power = 6
    toughness = 6
    oracleText = "Flying\nWhen this creature enters, if it was cast, each player exiles all but the " +
        "bottom six cards of their library face down.\nAt the beginning of your upkeep, draw a card."

    keywords(Keyword.FLYING)

    // Top (librarySize - 6) cards = everything except the bottom six. Clamp to >= 0 so a library of
    // six or fewer yields zero cards to exile (CR / ruling: nothing happens).
    val allButBottomSix = DynamicAmount.IfPositive(
        DynamicAmount.Subtract(
            DynamicAmount.Count(Player.You, Zone.LIBRARY),
            DynamicAmount.Fixed(6)
        )
    )

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        triggerCondition = Conditions.WasCast
        effect = ForEachPlayerEffect(
            players = Player.Each,
            effects = listOf(
                GatherCardsEffect(
                    source = CardSource.TopOfLibrary(allButBottomSix),
                    storeAs = "doomsdayExiled",
                    revealed = false,
                    lookAudience = LookAudience.None
                ),
                MoveCollectionEffect(
                    from = "doomsdayExiled",
                    destination = CardDestination.ToZone(Zone.EXILE),
                    faceDown = FaceDownMode.HIDDEN
                )
            )
        )
        description = "When this creature enters, if it was cast, each player exiles all but the " +
            "bottom six cards of their library face down."
    }

    triggeredAbility {
        trigger = Triggers.YourUpkeep
        effect = Effects.DrawCards(1)
        description = "At the beginning of your upkeep, draw a card."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "94"
        artist = "Denys Tsiperko"
        flavorText = "\"All stars grow dark. All worlds come to an end.\""
        imageUri = "https://cards.scryfall.io/normal/front/5/4/542c89b6-48c6-4fcb-8a4c-b5ed2fa1d384.jpg?1726286201"
        ruling(
            "2024-09-20",
            "A player with six or fewer cards remaining in their library when Doomsday Excruciator's " +
                "second ability resolves won't exile any cards."
        )
    }
}
