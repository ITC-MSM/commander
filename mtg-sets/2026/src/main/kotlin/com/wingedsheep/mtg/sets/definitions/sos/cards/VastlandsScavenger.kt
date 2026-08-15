package com.wingedsheep.mtg.sets.definitions.sos.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.effects.GatherCardsEffect
import com.wingedsheep.sdk.scripting.effects.MoveCollectionEffect
import com.wingedsheep.sdk.scripting.effects.SelectFromCollectionEffect
import com.wingedsheep.sdk.scripting.effects.SelectionMode
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Vastlands Scavenger // Bind to Life — Secrets of Strixhaven #166
 * {1}{G}{G} · Creature — Bear Druid · 4/4
 *
 * Deathtouch
 * This creature enters prepared. (While it's prepared, you may cast a copy of its spell.
 * Doing so unprepares it.)
 * //
 * Bind to Life — {4}{G}, Instant: Mill seven cards. Then put a creature card from among them
 * onto the battlefield.
 *
 * Prepare (Secrets of Strixhaven): the creature enters with [Keyword.PREPARED]; becoming prepared
 * creates a copy of its prepare spell ("Bind to Life") in exile that its controller may cast for
 * {4}{G}, and casting that copy unprepares the creature. Modeled via [CardLayout.PREPARE] +
 * the `prepare(name) { }` DSL.
 *
 * Bind to Life mills seven (gather top 7 → graveyard), then puts a creature card from among the
 * milled cards onto the battlefield: a [SelectFromCollectionEffect] of exactly one creature from
 * the milled collection (auto-selects none if no creature was milled) followed by
 * [MoveCollectionEffect] to the battlefield. The select reads the milled collection, which still
 * tracks the cards' refs after they hit the graveyard.
 */
val VastlandsScavenger = card("Vastlands Scavenger") {
    manaCost = "{1}{G}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Bear Druid"
    power = 4
    toughness = 4
    oracleText = "Deathtouch\nThis creature enters prepared. (While it's prepared, you may cast a " +
        "copy of its spell. Doing so unprepares it.)"

    keywords(Keyword.DEATHTOUCH, Keyword.PREPARED)

    // Bind to Life — the prepare spell. Mill seven; then put a milled creature card onto the battlefield.
    prepare("Bind to Life") {
        manaCost = "{4}{G}"
        typeLine = "Instant"
        oracleText = "Mill seven cards. Then put a creature card from among them onto the battlefield."
        spell {
            effect = Effects.Composite(
                listOf(
                    GatherCardsEffect(
                        source = CardSource.TopOfLibrary(DynamicAmount.Fixed(7)),
                        storeAs = "milled",
                    ),
                    MoveCollectionEffect(
                        from = "milled",
                        destination = CardDestination.ToZone(Zone.GRAVEYARD),
                    ),
                    SelectFromCollectionEffect(
                        from = "milled",
                        selection = SelectionMode.ChooseExactly(DynamicAmount.Fixed(1)),
                        filter = GameObjectFilter.Creature,
                        storeSelected = "toBattlefield",
                        showAllCards = true,
                        prompt = "Put a creature card onto the battlefield",
                    ),
                    MoveCollectionEffect(
                        from = "toBattlefield",
                        destination = CardDestination.ToZone(Zone.BATTLEFIELD),
                    ),
                ),
            )
        }
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "166"
        artist = "Bryan Sola"
        imageUri = "https://cards.scryfall.io/normal/front/4/7/476b6a4d-cc05-4e98-8a45-a5c6582ec514.jpg?1778165152"
    }
}
