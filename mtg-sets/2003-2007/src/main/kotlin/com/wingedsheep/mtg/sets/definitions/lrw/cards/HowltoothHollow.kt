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
 * Howltooth Hollow
 * Land
 *
 * Hideaway 4 (When this land enters, look at the top four cards of your library, exile
 * one face down, then put the rest on the bottom in a random order.)
 * This land enters tapped.
 * {T}: Add {B}.
 * {B}, {T}: You may play the exiled card without paying its mana cost if each player has
 * no cards in hand.
 *
 * Same shape as [MosswortBridge] — see its comment for how hideaway is composed. The only
 * difference is the gate on the free-cast ability: "each player has no cards in hand" is
 * modelled as the *summed* hand size across every seat being zero, which is exactly the
 * universal claim (a sum of non-negative counts is 0 iff every term is 0) and so stays
 * correct in multiplayer, where a per-opponent comparison would not.
 */
val HowltoothHollow = card("Howltooth Hollow") {
    typeLine = "Land"
    colorIdentity = "B"
    oracleText = "Hideaway 4 (When this land enters, look at the top four cards of your " +
        "library, exile one face down, then put the rest on the bottom in a random order.)\n" +
        "This land enters tapped.\n" +
        "{T}: Add {B}.\n" +
        "{B}, {T}: You may play the exiled card without paying its mana cost if each player " +
        "has no cards in hand."

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
        effect = AddManaEffect(Color.BLACK)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{B}"), Costs.Tap)
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
                    DynamicAmount.Count(Player.Each, Zone.HAND),
                    ComparisonOperator.EQ,
                    DynamicAmount.Fixed(0)
                )
            )
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "269"
        artist = "John Franklin Howe"
        imageUri = "https://cards.scryfall.io/normal/front/5/c/5c678643-6640-49e1-a5b0-2954123bcabc.jpg?1783942847"
        ruling("2022-04-29", "\"Hideaway N\" means \"When this permanent enters the battlefield, look at the top N cards of your library. Exile one of them face down and put the rest on the bottom of your library in a random order. The exiled card gains 'The player who controls the permanent that exiled this card may look at this card in the exile zone.'\"")
        ruling("2022-04-29", "Any player who has controlled a permanent with a hideaway ability since a card was exiled with it may look at that card.")
        ruling("2022-04-29", "Previously, permanents with hideaway entered the battlefield tapped. This ability has been removed from the definition of hideaway. Older cards have received errata to have an additional paragraph that reads \"[This permanent] enters the battlefield tapped,\" and they now have hideaway 4.")
        ruling("2022-04-29", "Hideaway now causes you to put the rest of the cards on the bottom of your library in a random order instead of any order.")
    }
}
