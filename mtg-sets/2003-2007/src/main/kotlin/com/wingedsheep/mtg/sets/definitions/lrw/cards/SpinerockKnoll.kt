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
import com.wingedsheep.sdk.scripting.values.TurnTracker

/**
 * Spinerock Knoll
 * Land
 *
 * Hideaway 4 (When this land enters, look at the top four cards of your library, exile
 * one face down, then put the rest on the bottom in a random order.)
 * This land enters tapped.
 * {T}: Add {R}.
 * {R}, {T}: You may play the exiled card without paying its mana cost if an opponent was
 * dealt 7 or more damage this turn.
 *
 * Same shape as [MosswortBridge] — see its comment for how hideaway is composed. The gate is
 * "an opponent was dealt 7 or more damage this turn": an *existential* over opponents, so it
 * reads [DynamicAmount.GreatestAmongPlayers] rather than a plain per-opponent tracker. Summing
 * `DAMAGE_RECEIVED` across `Player.EachOpponent` would let three opponents on 3 damage each
 * satisfy a 7-damage threshold no single one of them reached; taking the greatest measures each
 * opponent separately, which is what the card asks. Damage of any kind counts, combat or not
 * (2007-10-01 ruling), which is why this is `DAMAGE_RECEIVED` and not the combat-only tracker.
 */
val SpinerockKnoll = card("Spinerock Knoll") {
    typeLine = "Land"
    colorIdentity = "R"
    oracleText = "Hideaway 4 (When this land enters, look at the top four cards of your " +
        "library, exile one face down, then put the rest on the bottom in a random order.)\n" +
        "This land enters tapped.\n" +
        "{T}: Add {R}.\n" +
        "{R}, {T}: You may play the exiled card without paying its mana cost if an opponent " +
        "was dealt 7 or more damage this turn."

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
        effect = AddManaEffect(Color.RED)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{R}"), Costs.Tap)
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
                        players = Player.EachOpponent,
                        inner = DynamicAmount.TurnTracking(Player.You, TurnTracker.DAMAGE_RECEIVED)
                    ),
                    ComparisonOperator.GTE,
                    DynamicAmount.Fixed(7)
                )
            )
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "274"
        artist = "Steve Prescott"
        imageUri = "https://cards.scryfall.io/normal/front/d/e/dec6abed-8fbb-4d3e-8f89-64ea1b3913db.jpg?1783942847"
        ruling("2022-04-29", "\"Hideaway N\" means \"When this permanent enters the battlefield, look at the top N cards of your library. Exile one of them face down and put the rest on the bottom of your library in a random order. The exiled card gains 'The player who controls the permanent that exiled this card may look at this card in the exile zone.'\"")
        ruling("2022-04-29", "Any player who has controlled a permanent with a hideaway ability since a card was exiled with it may look at that card.")
        ruling("2022-04-29", "Previously, permanents with hideaway entered the battlefield tapped. This ability has been removed from the definition of hideaway. Older cards have received errata to have an additional paragraph that reads \"[This permanent] enters the battlefield tapped,\" and they now have hideaway 4.")
        ruling("2022-04-29", "Hideaway now causes you to put the rest of the cards on the bottom of your library in a random order instead of any order.")
        ruling("2007-10-01", "It doesn't matter how the opponent was dealt damage or by whom, as long as the total damage is 7 or more. You don't specify an opponent when you activate the ability.")
        ruling("2007-10-01", "You'll get to play the card even if Spinerock Knoll wasn't on the battlefield at the time some or all of the 7 damage was dealt.")
        ruling("2007-10-01", "At the time the last ability resolves, you'll get to play the card if a player who is currently your opponent, or a player who was your opponent at the time they left the game, has been dealt 7 damage over the course of the turn.")
    }
}
