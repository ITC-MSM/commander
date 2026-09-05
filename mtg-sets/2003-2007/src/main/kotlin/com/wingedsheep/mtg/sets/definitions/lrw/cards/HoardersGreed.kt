package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.CLASH_WON
import com.wingedsheep.sdk.scripting.effects.RepeatCondition
import com.wingedsheep.sdk.scripting.targets.EffectTarget

val HoardersGreed = card("Hoarder's Greed") {
    manaCost = "{3}{B}"
    colorIdentity = "B"
    typeLine = "Sorcery"
    oracleText = "You lose 2 life and draw two cards, then clash with an opponent. If you win, repeat this process. (Each clashing player reveals the top card of their library, then puts that card on their choice of the top or bottom. A player wins if their card had a greater mana value.)"

    spell {
        effect = Effects.RepeatWhile(
            body = Effects.Composite(
                Effects.LoseLife(2, EffectTarget.Controller),
                Effects.DrawCards(2),
                Patterns.Mechanic.clash()
            ),
            repeatCondition = RepeatCondition.WhileCondition(
                Conditions.CollectionContainsMatch(CLASH_WON)
            )
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "117"
        artist = "Pete Venters"
        imageUri = "https://cards.scryfall.io/normal/front/9/6/9666a328-cc51-4be1-abef-06bbe41dd866.jpg?1783942890"
        ruling("2007-10-01", "The effect will automatically repeat itself until its controller doesn't win the clash. There's no other way to stop it.")
    }
}
