package com.wingedsheep.mtg.sets.definitions.dgm.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersTapped
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.conditions.ComparisonOperator
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect
import com.wingedsheep.sdk.scripting.effects.SearchDestination
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.values.DynamicAmount

private val gate = GameObjectFilter.Land.withSubtype(Subtype.GATE.value)

/**
 * Maze's End
 * Land
 * This land enters tapped.
 * {T}: Add {C}.
 * {3}, {T}, Return this land to its owner's hand: Search your library for a Gate card, put it
 * onto the battlefield, then shuffle. If you control ten or more Gates with different names,
 * you win the game.
 *
 * Modeling notes:
 *  - Returning Maze's End to hand is part of the **cost** (`Costs.ReturnSelfToHand`), not the
 *    effect — per the 2013-04-15 ruling players can't respond between announcement and payment,
 *    and the ability resolves even though its source has left the battlefield.
 *  - The win check happens *as the ability resolves*, after the search, and happens even if no
 *    Gate was found (the search is "search for a Gate card", which may fail or be declined). It's
 *    modeled as a `ConditionalEffect` sequenced after the search rather than a separate trigger,
 *    so it is checked once, at resolution, and never at other times.
 *  - "ten or more Gates with different names" counts each name once, hence
 *    `distinctNames()` — controlling several Guildgates of the same name adds nothing.
 *  - Maze's End itself is a plain Land, not a Gate, so it never counts toward its own total.
 */
val MazesEnd = card("Maze's End") {
    manaCost = ""
    typeLine = "Land"
    oracleText = "This land enters tapped.\n{T}: Add {C}.\n{3}, {T}, Return this land to its " +
        "owner's hand: Search your library for a Gate card, put it onto the battlefield, then " +
        "shuffle. If you control ten or more Gates with different names, you win the game."

    replacementEffect(EntersTapped())

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddColorlessMana(1)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{3}"), Costs.Tap, Costs.ReturnSelfToHand)
        effect = Effects.Composite(
            Patterns.Library.searchLibrary(
                filter = gate,
                count = 1,
                destination = SearchDestination.BATTLEFIELD,
                shuffleAfter = true
            ),
            ConditionalEffect(
                condition = Conditions.CompareAmounts(
                    DynamicAmounts.battlefield(Player.You, gate).distinctNames(),
                    ComparisonOperator.GTE,
                    DynamicAmount.Fixed(10)
                ),
                effect = Effects.WinGame()
            )
        )
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "152"
        artist = "Cliff Childs"
        imageUri = "https://cards.scryfall.io/normal/front/4/0/401f7042-24fd-42a0-ae7c-e6b7de1aa446.jpg?1783940010"
        ruling("2013-04-15", "Returning Maze's End to its owner's hand is part of the cost to activate its last ability. Once that ability is announced, players can't respond to it until after you've paid its activation cost and returned Maze's End to hand.")
        ruling("2013-04-15", "When the last ability of Maze's End resolves, you'll search for a Gate and put it onto the battlefield before checking to see if you win the game. This check will happen even if you don't put a Gate onto the battlefield this way. This check will happen only as the ability resolves, not at other times.")
        ruling("2013-04-15", "Putting a Gate onto the battlefield with Maze's End doesn't count as the one land you can play during your turn. If it's your turn, you can play Maze's End or a different land card from your hand after its ability has resolved.")
        ruling("2013-04-15", "Controlling multiple Gates with the same name has no effect on your ability to win the game with Maze's End. The excess Gates are simply ignored.")
    }
}
