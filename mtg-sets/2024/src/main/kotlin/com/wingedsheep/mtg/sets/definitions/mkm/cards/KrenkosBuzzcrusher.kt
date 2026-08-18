package com.wingedsheep.mtg.sets.definitions.mkm.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.effects.Chooser
import com.wingedsheep.sdk.scripting.effects.MayEffect
import com.wingedsheep.sdk.scripting.effects.MoveType
import com.wingedsheep.sdk.scripting.effects.SearchDestination
import com.wingedsheep.sdk.scripting.predicates.CardPredicate
import com.wingedsheep.sdk.scripting.references.Player

/**
 * Krenko's Buzzcrusher — Murders at Karlov Manor #136
 * {2}{R}{R} · Artifact Creature — Insect Thopter · Rare
 * 4/4
 *
 * The lands are deliberately selected during resolution rather than declared as targets. One
 * [Effects.ForEachPlayer] iteration rebinds `Player.You` to that player while preserving
 * [Chooser.SourceController] for the Buzzcrusher controller's choice. [moveTracked] distinguishes
 * a land actually destroyed from an indestructible one, so only a successful destruction offers
 * that land's controller the compensating basic-land search.
 */
val KrenkosBuzzcrusher = card("Krenko's Buzzcrusher") {
    manaCost = "{2}{R}{R}"
    colorIdentity = "R"
    typeLine = "Artifact Creature — Insect Thopter"
    oracleText = "Flying, trample\n" +
        "When this creature enters, for each player, destroy up to one nonbasic land that player " +
        "controls. For each land destroyed this way, its controller may search their library for " +
        "a basic land card, put it onto the battlefield tapped, then shuffle."
    power = 4
    toughness = 4

    keywords(Keyword.FLYING, Keyword.TRAMPLE)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.ForEachPlayer(
            Player.Each,
            listOf(
                Effects.Pipeline {
                    val lands = gather(
                        CardSource.FromZone(
                            Zone.BATTLEFIELD,
                            Player.You,
                            GameObjectFilter(
                                cardPredicates = listOf(
                                    CardPredicate.IsLand,
                                    CardPredicate.Not(CardPredicate.IsBasicLand),
                                ),
                            ),
                        ),
                    )
                    val chosen = chooseUpTo(
                        1,
                        from = lands,
                        chooser = Chooser.SourceController,
                        prompt = "Choose up to one nonbasic land this player controls to destroy",
                        useTargetingUI = true,
                        alwaysPrompt = true,
                    )
                    val destroyed = moveTracked(
                        chosen,
                        CardDestination.ToZone(Zone.GRAVEYARD),
                        moveType = MoveType.Destroy,
                    )
                    ifNotEmpty(destroyed) {
                        run(
                            MayEffect(
                                Patterns.Library.searchLibrary(
                                    filter = GameObjectFilter.BasicLand,
                                    count = 1,
                                    destination = SearchDestination.BATTLEFIELD,
                                    entersTapped = true,
                                ),
                            ),
                        )
                    }
                },
            ),
        )
        description = "For each player, destroy up to one nonbasic land that player controls. For " +
            "each land destroyed this way, its controller may search their library for a basic " +
            "land card, put it onto the battlefield tapped, then shuffle."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "136"
        artist = "Joshua Raphael"
        imageUri = "https://cards.scryfall.io/normal/front/0/e/0edcda2a-071b-40c5-9fb3-8a4ff87ca00e.jpg?1783912878"

        ruling(
            "2024-02-02",
            "None of the nonbasic lands are targets of Krenko's Buzzcrusher's triggered ability.",
        )
    }
}
