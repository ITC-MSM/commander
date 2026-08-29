package com.wingedsheep.mtg.sets.definitions.thb.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.conditions.Compare
import com.wingedsheep.sdk.scripting.conditions.ComparisonOperator
import com.wingedsheep.sdk.scripting.effects.CardOrder
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Thassa's Oracle
 * {U}{U}
 * Creature — Merfolk Wizard 1/3
 * When this creature enters, look at the top X cards of your library, where X is your devotion to
 * blue. Put up to one of them on top of your library and the rest on the bottom of your library in
 * a random order. If X is greater than or equal to the number of cards in your library, you win
 * the game.
 *
 * X is [DynamicAmount.DevotionTo] blue, evaluated twice because the card names it twice — once as
 * the size of the look, once as the left side of the win check. Both reads happen during the same
 * resolution, so they agree.
 *
 * The win check is [Compare] `GTE` against [DynamicAmount.AggregateZone] over the library, and it
 * runs *after* the pipeline has put the looked-at cards back — which is what makes the count
 * correct: the cards are still in the library when the comparison is made, exactly as the printed
 * sentence order says.
 *
 * Devotion 0 needs no special case. [CardSource.TopOfLibrary] with a count of zero gathers nothing,
 * the choose-and-put-back steps are no-ops, and the comparison `0 >= 0` still wins on an empty
 * library — the 2020-01-24 ruling.
 */
val ThassasOracle = card("Thassa's Oracle") {
    manaCost = "{U}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Merfolk Wizard"
    power = 1
    toughness = 3
    oracleText = "When this creature enters, look at the top X cards of your library, where X is " +
        "your devotion to blue. Put up to one of them on top of your library and the rest on the " +
        "bottom of your library in a random order. If X is greater than or equal to the number of " +
        "cards in your library, you win the game."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.Composite(
            Effects.Pipeline {
                val looked = gather(
                    CardSource.TopOfLibrary(
                        count = DynamicAmount.DevotionTo(listOf(Color.BLUE)),
                        player = Player.You
                    )
                )
                val split = chooseUpToSplit(
                    count = 1,
                    from = looked,
                    prompt = "Put up to one card on top of your library",
                    selectedLabel = "Put on top of library",
                    remainderLabel = "Put on the bottom in a random order"
                )
                toLibraryTop(split.selected)
                toLibraryBottom(split.remainder, order = CardOrder.Random)
            },
            ConditionalEffect(
                condition = Compare(
                    DynamicAmount.DevotionTo(listOf(Color.BLUE)),
                    ComparisonOperator.GTE,
                    DynamicAmount.AggregateZone(Player.You, Zone.LIBRARY)
                ),
                effect = Effects.WinGame(
                    message = "Thassa's Oracle: devotion to blue was at least the number of cards " +
                        "in your library."
                )
            )
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "73"
        artist = "Jesper Ejsing"
        imageUri = "https://cards.scryfall.io/normal/front/7/2/726e8b29-13e9-4138-b6a9-d2a0d8188d1c.jpg?1783931575"

        ruling(
            "2020-01-24",
            "If your devotion to blue is zero at the time the triggered ability of Thassa's Oracle " +
                "resolves, you don't look at or move any cards in your library. If you have no " +
                "cards in your library, you win the game."
        )
        ruling(
            "2020-01-24",
            "Hybrid mana symbols, monocolored hybrid mana symbols, and Phyrexian mana symbols do " +
                "count toward your devotion to their color(s)."
        )
        ruling(
            "2020-01-24",
            "If an activated ability or triggered ability has an effect that depends on your " +
                "devotion to a color, you count the number of mana symbols of that color among the " +
                "mana costs of permanents you control as the ability resolves. The permanent with " +
                "that ability will be counted if it's still on the battlefield at that time."
        )
    }
}
