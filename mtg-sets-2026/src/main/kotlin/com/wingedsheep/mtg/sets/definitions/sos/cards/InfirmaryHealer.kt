package com.wingedsheep.mtg.sets.definitions.sos.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Infirmary Healer // Stream of Life — Secrets of Strixhaven #152
 * {1}{G} · Creature — Cat Cleric · 2/3
 *
 * This creature enters prepared. (While it's prepared, you may cast a copy of its spell.
 * Doing so unprepares it.)
 * //
 * Stream of Life — {X}{G}, Sorcery: Target player gains X life.
 *
 * Prepare (Secrets of Strixhaven): the creature enters with the PREPARED keyword. Becoming
 * prepared creates a copy of its prepare spell ("Stream of Life") in exile that its controller
 * may cast for {X}{G}; casting that copy unprepares the creature. Modeled via [CardLayout.PREPARE]
 * + the `prepare(name) { }` DSL. The X chosen for the copy's cast feeds [DynamicAmount.XValue].
 */
val InfirmaryHealer = card("Infirmary Healer") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Cat Cleric"
    power = 2
    toughness = 3
    oracleText = "This creature enters prepared. (While it's prepared, you may cast a copy of its spell. Doing so unprepares it.)"

    keywords(Keyword.PREPARED)

    // Stream of Life — the prepare spell. Target player gains X life.
    prepare("Stream of Life") {
        manaCost = "{X}{G}"
        typeLine = "Sorcery"
        oracleText = "Target player gains X life."
        spell {
            target = Targets.Player
            effect = Effects.GainLife(DynamicAmount.XValue, EffectTarget.ContextTarget(0))
        }
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "152"
        artist = "Nereida"
        imageUri = "https://cards.scryfall.io/normal/front/9/1/911442e3-3003-4683-a766-e791e9553667.jpg?1775938036"
    }
}
