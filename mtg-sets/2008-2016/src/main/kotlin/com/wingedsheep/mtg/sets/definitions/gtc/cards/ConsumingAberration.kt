package com.wingedsheep.mtg.sets.definitions.gtc.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.GatherUntilMatchEffect
import com.wingedsheep.sdk.scripting.effects.MoveCollectionEffect
import com.wingedsheep.sdk.scripting.effects.RevealCollectionEffect
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Consuming Aberration
 * {3}{U}{B}
 * Creature — Horror
 * ✶/✶
 *
 * Consuming Aberration's power and toughness are each equal to the number of cards in your
 * opponents' graveyards.
 * Whenever you cast a spell, each opponent reveals cards from the top of their library until
 * they reveal a land card, then puts those cards into their graveyard.
 *
 * The characteristic-defining P/T is [dynamicStats] with a [DynamicAmount.Count] over all
 * opponents' graveyards (Soulless One pattern). The spell-cast trigger wraps the
 * GatherUntilMatch + RevealCollection + MoveCollection pipeline (Sméagol, Helpful Guide
 * pattern) in [Effects.ForEachPlayer] so each opponent walks and bins their own library —
 * the revealed cards, land included, go to that opponent's graveyard.
 */
val ConsumingAberration = card("Consuming Aberration") {
    manaCost = "{3}{U}{B}"
    colorIdentity = "UB"
    typeLine = "Creature — Horror"
    oracleText = "Consuming Aberration's power and toughness are each equal to the number of cards in your opponents' graveyards.\n" +
        "Whenever you cast a spell, each opponent reveals cards from the top of their library until they reveal a land card, then puts those cards into their graveyard."

    dynamicStats(
        DynamicAmount.Count(Player.EachOpponent, Zone.GRAVEYARD, GameObjectFilter.Any)
    )

    triggeredAbility {
        trigger = Triggers.YouCastSpell
        effect = Effects.ForEachPlayer(
            Player.EachOpponent,
            listOf(
                GatherUntilMatchEffect(
                    player = Player.You,
                    filter = GameObjectFilter.Land,
                    storeMatch = "revealedLand",
                    storeRevealed = "allRevealed"
                ),
                RevealCollectionEffect(from = "allRevealed"),
                MoveCollectionEffect(
                    from = "allRevealed",
                    destination = CardDestination.ToZone(Zone.GRAVEYARD, player = Player.You)
                )
            )
        )
        description = "Whenever you cast a spell, each opponent reveals cards from the top of their library until they reveal a land card, then puts those cards into their graveyard."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "152"
        artist = "Karl Kopinski"
        imageUri = "https://cards.scryfall.io/normal/front/6/3/6354de66-f7f8-4e33-98d0-52624d3d7828.jpg?1783940111"
    }
}
