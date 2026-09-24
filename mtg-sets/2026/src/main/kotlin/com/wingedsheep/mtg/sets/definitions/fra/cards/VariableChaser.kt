package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.ForEachPlayerEffect
import com.wingedsheep.sdk.scripting.effects.MayEffect
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Variable Chaser // Arc of Fortune — each player, in turn order starting with you, chooses whether
 * to discard their hand and draw seven (Raphael's Technique's shape).
 */
val VariableChaser = card("Variable Chaser") {
    manaCost = "{2}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Human Wizard"
    power = 2
    toughness = 3
    oracleText = "Flying, prowess\n" +
        "This creature enters prepared. (While it's prepared, you may cast a copy of its spell. Doing so " +
        "unprepares it.)"

    keywords(Keyword.FLYING, Keyword.PROWESS, Keyword.PREPARED)

    prepare("Arc of Fortune") {
        manaCost = "{2}{U}"
        typeLine = "Sorcery"
        oracleText = "Each player may discard their hand and draw seven cards."
        spell {
            effect = ForEachPlayerEffect(
                players = Player.Each,
                effects = listOf(
                    MayEffect(
                        decisionMaker = EffectTarget.Controller,
                        effect = Effects.Composite(
                            Patterns.Hand.discardHand(EffectTarget.Controller),
                            Effects.DrawCards(7)
                        )
                    )
                )
            )
        }
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "47"
        artist = "Kai Carpenter"
        imageUri = "https://cards.scryfall.io/normal/front/e/3/e3afedb1-bf9d-4e31-9700-433514cc29b1.jpg?1789470807"
        inBooster = false
    }
}
