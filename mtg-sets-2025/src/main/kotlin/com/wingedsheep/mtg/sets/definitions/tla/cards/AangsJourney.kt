package com.wingedsheep.mtg.sets.definitions.tla.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.conditions.WasKicked
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect
import com.wingedsheep.sdk.scripting.effects.GatherCardsEffect
import com.wingedsheep.sdk.scripting.effects.MoveCollectionEffect
import com.wingedsheep.sdk.scripting.effects.SearchDestination
import com.wingedsheep.sdk.scripting.effects.SelectFromCollectionEffect
import com.wingedsheep.sdk.scripting.effects.SelectionMode
import com.wingedsheep.sdk.scripting.effects.ShuffleLibraryEffect
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Aang's Journey
 * {2}
 * Sorcery — Lesson
 *
 * Kicker {2} (You may pay an additional {2} as you cast this spell.)
 * Search your library for a basic land card. If this spell was kicked, instead search your
 * library for a basic land card and a Shrine card. Reveal those cards, put them into your
 * hand, then shuffle.
 * You gain 2 life.
 *
 * The kicker swaps the entire search clause, so it is modeled as a [ConditionalEffect] keyed
 * on [WasKicked] (resolution-time state test, no decision/pause): the unkicked branch is the
 * ordinary single basic-land tutor-to-hand ([Patterns.Library.searchLibrary]); the kicked
 * branch adds a second selection for a Shrine card, then reveals + moves both finds to hand
 * with a single shuffle afterward. All selections are `ChooseUpTo(1)` because a library search
 * may legally fail to find (CR 701.19). The +2 life always follows, regardless of kicker.
 */
val AangsJourney = card("Aang's Journey") {
    manaCost = "{2}"
    colorIdentity = ""
    typeLine = "Sorcery — Lesson"
    oracleText = "Kicker {2} (You may pay an additional {2} as you cast this spell.)\n" +
        "Search your library for a basic land card. If this spell was kicked, instead search " +
        "your library for a basic land card and a Shrine card. Reveal those cards, put them " +
        "into your hand, then shuffle.\n" +
        "You gain 2 life."

    keywordAbility(KeywordAbility.kicker("{2}"))

    spell {
        val kickedSearch = Effects.Composite(
            listOf(
                GatherCardsEffect(
                    source = CardSource.FromZone(Zone.LIBRARY, Player.You, GameObjectFilter.BasicLand),
                    storeAs = "landSearchable"
                ),
                SelectFromCollectionEffect(
                    from = "landSearchable",
                    selection = SelectionMode.ChooseUpTo(DynamicAmount.Fixed(1)),
                    storeSelected = "foundLand",
                    prompt = "Search your library for a basic land card"
                ),
                GatherCardsEffect(
                    source = CardSource.FromZone(
                        Zone.LIBRARY,
                        Player.You,
                        GameObjectFilter.Any.withSubtype("Shrine")
                    ),
                    storeAs = "shrineSearchable"
                ),
                SelectFromCollectionEffect(
                    from = "shrineSearchable",
                    selection = SelectionMode.ChooseUpTo(DynamicAmount.Fixed(1)),
                    storeSelected = "foundShrine",
                    prompt = "Search your library for a Shrine card"
                ),
                MoveCollectionEffect(
                    from = "foundLand",
                    destination = CardDestination.ToZone(Zone.HAND),
                    revealed = true
                ),
                MoveCollectionEffect(
                    from = "foundShrine",
                    destination = CardDestination.ToZone(Zone.HAND),
                    revealed = true
                ),
                ShuffleLibraryEffect()
            )
        )

        effect = ConditionalEffect(
            condition = WasKicked,
            effect = kickedSearch,
            elseEffect = Patterns.Library.searchLibrary(
                filter = GameObjectFilter.BasicLand,
                count = 1,
                destination = SearchDestination.HAND,
                reveal = true,
                shuffleAfter = true
            )
        ).then(Effects.GainLife(2))
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "1"
        artist = "Kotakan"
        imageUri = "https://cards.scryfall.io/normal/front/5/e/5e51f727-5a9b-4bc7-83a9-dbcf1c933e15.jpg?1778833139"
    }
}
