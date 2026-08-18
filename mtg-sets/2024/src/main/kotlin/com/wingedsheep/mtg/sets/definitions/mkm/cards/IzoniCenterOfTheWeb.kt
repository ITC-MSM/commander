package com.wingedsheep.mtg.sets.definitions.mkm.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.OptionalCostEffect

/**
 * Izoni, Center of the Web
 * {4}{B}{G}
 * Legendary Creature — Elf Detective
 * 5/4
 *
 * Menace
 * Whenever Izoni enters or attacks, you may collect evidence 4. If you do, create two 2/1 black
 * and green Spider creature tokens with reach and menace.
 * Sacrifice four tokens: Surveil 2, then draw two cards. You gain 2 life.
 *
 * "Enters or attacks" is represented by two triggered abilities sharing the same immutable payoff.
 * The ordinary, non-reflexive "if you do" clause is an [OptionalCostEffect]: collecting evidence is
 * the optional payment, and the tokens are created only after that payment completes. The activated
 * ability pays its four-token sacrifice before resolving the surveil, draw, and life-gain sequence.
 */
val IzoniCenterOfTheWeb = card("Izoni, Center of the Web") {
    manaCost = "{4}{B}{G}"
    colorIdentity = "BG"
    typeLine = "Legendary Creature — Elf Detective"
    oracleText = "Menace\n" +
        "Whenever Izoni enters or attacks, you may collect evidence 4. If you do, create two 2/1 " +
        "black and green Spider creature tokens with reach and menace.\n" +
        "Sacrifice four tokens: Surveil 2, then draw two cards. You gain 2 life."
    power = 5
    toughness = 4

    keywords(Keyword.MENACE)

    val collectAndCreateSpiders = OptionalCostEffect(
        cost = Effects.CollectEvidence(4),
        ifPaid = Effects.CreateToken(
            power = 2,
            toughness = 1,
            colors = setOf(Color.BLACK, Color.GREEN),
            creatureTypes = setOf("Spider"),
            keywords = setOf(Keyword.REACH, Keyword.MENACE),
            count = 2,
            imageUri = "https://cards.scryfall.io/normal/front/7/4/74cb3ba3-9a14-4d72-bf24-0b75ae747f96.jpg?1783912607"
        )
    )

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = collectAndCreateSpiders
        description = "Whenever Izoni enters or attacks, you may collect evidence 4. If you do, " +
            "create two 2/1 black and green Spider creature tokens with reach and menace."
    }

    triggeredAbility {
        trigger = Triggers.Attacks
        effect = collectAndCreateSpiders
        description = "Whenever Izoni enters or attacks, you may collect evidence 4. If you do, " +
            "create two 2/1 black and green Spider creature tokens with reach and menace."
    }

    activatedAbility {
        cost = Costs.SacrificeMultiple(4, GameObjectFilter.Token)
        effect = Effects.Composite(
            Patterns.Library.surveil(2),
            Effects.DrawCards(2),
            Effects.GainLife(2)
        )
        description = "Sacrifice four tokens: Surveil 2, then draw two cards. You gain 2 life."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "209"
        artist = "Justine Cruz"
        imageUri = "https://cards.scryfall.io/normal/front/7/0/70ea66cd-587a-4ca6-87f7-a52c96079e4a.jpg?1783912846"

        ruling(
            "2024-02-02",
            "If you can't exile enough cards to meet or exceed the required mana value, you " +
                "can't choose to collect evidence at all."
        )
    }
}
