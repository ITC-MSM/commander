package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ActivationRestriction
import com.wingedsheep.sdk.scripting.EntersTapped
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.conditions.Compare
import com.wingedsheep.sdk.scripting.conditions.ComparisonOperator
import com.wingedsheep.sdk.scripting.effects.AddManaEffect
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.CardOrder
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.effects.FaceDownMode
import com.wingedsheep.sdk.scripting.effects.GatherCardsEffect
import com.wingedsheep.sdk.scripting.effects.GrantMayPlayFromExileEffect
import com.wingedsheep.sdk.scripting.effects.GrantPlayWithoutPayingCostEffect
import com.wingedsheep.sdk.scripting.effects.MoveCollectionEffect
import com.wingedsheep.sdk.scripting.effects.SelectFromCollectionEffect
import com.wingedsheep.sdk.scripting.effects.SelectionMode
import com.wingedsheep.sdk.scripting.effects.ZonePlacement
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Shelldock Isle
 * Land
 *
 * Hideaway 4 (When this land enters, look at the top four cards of your library, exile
 * one face down, then put the rest on the bottom in a random order.)
 * This land enters tapped.
 * {T}: Add {U}.
 * {U}, {T}: You may play the exiled card without paying its mana cost if a library has
 * twenty or fewer cards in it.
 *
 * The hideaway half is [SpinerockKnoll]'s shape verbatim — see its comment for how the ETB is
 * composed out of gather → select → move.
 *
 * The gate is the interesting line. **"A library" is an existential over *every* player**,
 * including you (2007-10-01: "It doesn't matter which library has twenty or fewer cards in it,
 * and you don't have to specify a library"), and the SDK's only player-boundary aggregation is
 * [DynamicAmount.GreatestAmongPlayers], which takes a *maximum*. A minimum is what this sentence
 * wants and there is no `LeastAmongPlayers`, so the composition takes the maximum of a **0/1
 * indicator** instead: each player is measured on their own ("does *my* library hold 20 or
 * fewer?"), and the greatest of those indicators is 1 exactly when at least one player qualifies.
 *
 * The wrong spellings both read plausibly on the card and both fail:
 *  - `AggregateZone(Player.Each, Zone.LIBRARY)` sums every library into one number, so two
 *    players on 15 cards each would report 30 and never satisfy the gate.
 *  - `AggregateZone(Player.You, Zone.LIBRARY)` silently narrows "a library" to yours, which is
 *    the *opposite* half of the card's real use — you activate this off an opponent milling out.
 *
 * [DynamicAmount.GreatestAmongPlayers] rebinds the resolution context's controller per iteration,
 * so [Player.You] inside the indicator means "the player being measured" — never write an
 * outward reference such as `Player.AnOpponent` there.
 *
 * Modelling the clause as an [ActivationRestriction] rather than a resolution-time check is the
 * same choice the rest of the hideaway cycle makes: a fail-closed gate means the ability is not
 * offered at all below the threshold, which is what the card's "if" does.
 */
val ShelldockIsle = card("Shelldock Isle") {
    typeLine = "Land"
    colorIdentity = "U"
    oracleText = "Hideaway 4 (When this land enters, look at the top four cards of your " +
        "library, exile one face down, then put the rest on the bottom in a random order.)\n" +
        "This land enters tapped.\n" +
        "{T}: Add {U}.\n" +
        "{U}, {T}: You may play the exiled card without paying its mana cost if a library has " +
        "twenty or fewer cards in it."

    keywordAbility(KeywordAbility.hideaway(4))

    replacementEffect(EntersTapped())

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.Composite(
            listOf(
                GatherCardsEffect(
                    source = CardSource.TopOfLibrary(
                        count = DynamicAmount.Fixed(4),
                        player = Player.You
                    ),
                    storeAs = "hideawayTop"
                ),
                SelectFromCollectionEffect(
                    from = "hideawayTop",
                    selection = SelectionMode.ChooseExactly(DynamicAmount.Fixed(1)),
                    storeSelected = "hideawayPicked",
                    storeRemainder = "hideawayRest",
                    prompt = "Choose a card to exile face down",
                    selectedLabel = "Exile face down",
                    remainderLabel = "Put on bottom of library"
                ),
                MoveCollectionEffect(
                    from = "hideawayPicked",
                    destination = CardDestination.ToZone(Zone.EXILE),
                    faceDown = FaceDownMode.HIDDEN,
                    linkToSource = true
                ),
                MoveCollectionEffect(
                    from = "hideawayRest",
                    destination = CardDestination.ToZone(Zone.LIBRARY, placement = ZonePlacement.Bottom),
                    order = CardOrder.Random
                )
            )
        )
    }

    activatedAbility {
        cost = Costs.Tap
        effect = AddManaEffect(Color.BLUE)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{U}"), Costs.Tap)
        effect = Effects.Composite(
            listOf(
                GatherCardsEffect(
                    source = CardSource.FromLinkedExile(),
                    storeAs = "hideawayLinked"
                ),
                GrantMayPlayFromExileEffect("hideawayLinked"),
                GrantPlayWithoutPayingCostEffect("hideawayLinked")
            )
        )
        restrictions = listOf(
            ActivationRestriction.OnlyIfCondition(
                Compare(
                    DynamicAmount.GreatestAmongPlayers(
                        players = Player.Each,
                        inner = DynamicAmount.Conditional(
                            condition = Compare(
                                DynamicAmount.AggregateZone(Player.You, Zone.LIBRARY),
                                ComparisonOperator.LTE,
                                DynamicAmount.Fixed(20)
                            ),
                            ifTrue = DynamicAmount.Fixed(1),
                            ifFalse = DynamicAmount.Fixed(0)
                        )
                    ),
                    ComparisonOperator.GTE,
                    DynamicAmount.Fixed(1)
                )
            )
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "272"
        artist = "Mark Tedin"
        imageUri = "https://cards.scryfall.io/normal/front/4/2/4216656e-90e8-45fc-a0f6-0d0d79d0a021.jpg?1783942847"
        ruling("2022-04-29", "\"Hideaway N\" means \"When this permanent enters the battlefield, look at the top N cards of your library. Exile one of them face down and put the rest on the bottom of your library in a random order. The exiled card gains 'The player who controls the permanent that exiled this card may look at this card in the exile zone.'\"")
        ruling("2022-04-29", "Any player who has controlled a permanent with a hideaway ability since a card was exiled with it may look at that card.")
        ruling("2022-04-29", "Previously, permanents with hideaway entered the battlefield tapped. This ability has been removed from the definition of hideaway. Older cards have received errata to have an additional paragraph that reads \"[This permanent] enters the battlefield tapped,\" and they now have hideaway 4.")
        ruling("2022-04-29", "Hideaway now causes you to put the rest of the cards on the bottom of your library in a random order instead of any order.")
        ruling("2007-10-01", "It doesn't matter which library has twenty or fewer cards in it, and you don't have to specify a library.")
    }
}
