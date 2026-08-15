package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.ForceSacrificeEffect
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Summon: Anima
 * {4}{B}{B}
 * Enchantment Creature — Saga Horror
 * 4/4
 * (As this Saga enters and after your draw step, add a lore counter. Sacrifice after IV.)
 * I, II, III — Pain — You draw a card and you lose 1 life.
 * IV — Oblivion — Each opponent sacrifices a creature of their choice and loses 3 life.
 * Menace
 */
val SummonAnima = card("Summon: Anima") {
    manaCost = "{4}{B}{B}"
    colorIdentity = "B"
    typeLine = "Enchantment Creature — Saga Horror"
    oracleText = "(As this Saga enters and after your draw step, add a lore counter. Sacrifice after IV.)\n" +
        "I, II, III — Pain — You draw a card and you lose 1 life.\n" +
        "IV — Oblivion — Each opponent sacrifices a creature of their choice and loses 3 life.\n" +
        "Menace"
    power = 4
    toughness = 4

    keywords(Keyword.MENACE)

    val pain = Effects.Composite(
        Effects.DrawCards(1),
        Effects.LoseLife(1, EffectTarget.Controller),
    )

    sagaChapter(1) { effect = pain }
    sagaChapter(2) { effect = pain }
    sagaChapter(3) { effect = pain }
    sagaChapter(4) {
        effect = Effects.Composite(
            ForceSacrificeEffect(GameObjectFilter.Creature, 1, EffectTarget.PlayerRef(Player.EachOpponent)),
            Effects.LoseLife(3, EffectTarget.PlayerRef(Player.EachOpponent)),
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "120"
        artist = "Yongjae Choi"
        imageUri = "https://cards.scryfall.io/normal/front/a/a/aa4f6703-21f8-4c29-ad5a-5afb54188ade.jpg?1748706213"
    }
}
