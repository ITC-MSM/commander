package com.wingedsheep.mtg.sets.definitions.tla.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ConditionalStaticAbility
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Messenger Hawk
 * {2}{U/B}
 * Creature — Bird Scout
 * 1/2
 *
 * Flying
 * When this creature enters, create a Clue token. (It's an artifact with
 * "{2}, Sacrifice this token: Draw a card.")
 * This creature gets +2/+0 as long as you've drawn two or more cards this turn.
 *
 * The conditional pump is a [ConditionalStaticAbility] wrapping a source-scoped
 * [ModifyStats] gated on [Conditions.YouDrewCardsThisTurn] (threshold 2) — the
 * cards-drawn-this-turn tracker is read each time projected state is computed, so the
 * +2/+0 appears the moment you've drawn your second card and reverts at end of turn.
 */
val MessengerHawk = card("Messenger Hawk") {
    manaCost = "{2}{U/B}"
    colorIdentity = "UB"
    typeLine = "Creature — Bird Scout"
    power = 1
    toughness = 2
    oracleText = "Flying\n" +
        "When this creature enters, create a Clue token. (It's an artifact with " +
        "\"{2}, Sacrifice this token: Draw a card.\")\n" +
        "This creature gets +2/+0 as long as you've drawn two or more cards this turn."

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.CreateClue()
    }

    staticAbility {
        ability = ConditionalStaticAbility(
            ability = ModifyStats(powerBonus = 2, toughnessBonus = 0, filter = GroupFilter.source()),
            condition = Conditions.YouDrewCardsThisTurn(2),
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "234"
        artist = "Daniel Romanovsky"
        flavorText = "\"Hawky, welcome to Team Avatar.\"\n—Sokka"
        imageUri = "https://cards.scryfall.io/normal/front/6/a/6a2946c0-0a40-4cdb-92c8-af2bafecca09.jpg?1764121735"
    }
}
