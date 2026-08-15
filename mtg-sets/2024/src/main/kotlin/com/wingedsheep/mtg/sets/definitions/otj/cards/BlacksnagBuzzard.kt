package com.wingedsheep.mtg.sets.definitions.otj.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersWithCounters
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.events.CounterTypeFilter

/**
 * Blacksnag Buzzard
 * {2}{B}
 * Creature — Bird
 * 2/1
 * Flying
 * This creature enters with a +1/+1 counter on it if a creature died this turn.
 * Plot {1}{B}
 */
val BlacksnagBuzzard = card("Blacksnag Buzzard") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Bird"
    power = 2
    toughness = 1
    oracleText = "Flying\n" +
        "This creature enters with a +1/+1 counter on it if a creature died this turn.\n" +
        "Plot {1}{B} (You may pay {1}{B} and exile this card from your hand. Cast it as a sorcery on a later turn without paying its mana cost. Plot only as a sorcery.)"

    keywords(Keyword.FLYING)
    keywordAbility(KeywordAbility.plot("{1}{B}"))

    replacementEffect(EntersWithCounters(
        counterType = CounterTypeFilter.PlusOnePlusOne,
        count = 1,
        selfOnly = true,
        condition = Conditions.CreatureDiedThisTurn
    ))

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "79"
        artist = "Michele Giorgi"
        imageUri = "https://cards.scryfall.io/normal/front/e/f/ef10b2ad-9b9b-4c5d-a2c7-3ce742224b50.jpg?1712355550"

        ruling("2024-04-12", "Plot abilities are written \"Plot [cost],\" which means \"Any time you have priority during your main phase while the stack is empty, you may pay [cost] and exile this card from your hand. It becomes plotted.\"")
        ruling("2024-04-12", "Exiling a card using its plot ability is a special action. Once you announce you're taking that action, no other player can respond by trying to remove that card from your hand.")
        ruling("2024-04-12", "You can't cast a plotted card on the same turn it became plotted. On any future turn, you may cast that card from exile without paying its mana cost during your main phase while the stack is empty.")
        ruling("2024-04-12", "If you're casting a plotted card from exile without paying its mana cost, you can't choose to cast it for any other alternative costs. You can, however, pay additional costs, such as kicker costs. If the plotted card has any mandatory additional costs, those must still be paid to cast the spell.")
        ruling("2024-04-12", "If a plotted card has {X} in its mana cost, you must choose 0 as the value of X when casting it without paying its mana cost.")
    }
}
