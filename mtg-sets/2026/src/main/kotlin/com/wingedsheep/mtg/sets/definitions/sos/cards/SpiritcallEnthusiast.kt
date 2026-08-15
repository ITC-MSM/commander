package com.wingedsheep.mtg.sets.definitions.sos.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.ForEachTargetEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Spiritcall Enthusiast // Scrollboost — Secrets of Strixhaven #33
 * {2}{W} · Creature — Cat Cleric · 3/3
 *
 * Whenever one or more tokens you control enter, this creature becomes prepared.
 * (While it's prepared, you may cast a copy of its spell. Doing so unprepares it.)
 * //
 * Scrollboost — {1}{W} · Sorcery: One or two target creatures each get +2/+2 until end of turn.
 *
 * Prepare (Secrets of Strixhaven): Spiritcall Enthusiast does NOT enter prepared (no PREPARED
 * keyword). It becomes prepared via its trigger — whenever one or more tokens you control enter —
 * through [Effects.BecomePrepared]. Becoming prepared creates a copy of its prepare spell
 * ("Scrollboost") in exile that its controller may cast for {1}{W}; casting that copy unprepares
 * the creature. Modeled via `CardLayout.PREPARE` + the `prepare(name) { }` DSL, like Leech
 * Collector. The token-enter trigger is the shared `OneOrMorePermanentsEnter(Token)` batch enter
 * (defaults to "you control"). Scrollboost's "one or two target creatures" is the variable-count
 * `TargetCreature(count = 2, minCount = 1)` fanned out by [ForEachTargetEffect].
 */
val SpiritcallEnthusiast = card("Spiritcall Enthusiast") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Cat Cleric"
    power = 3
    toughness = 3
    oracleText = "Whenever one or more tokens you control enter, this creature becomes prepared. (While it's prepared, you may cast a copy of its spell. Doing so unprepares it.)"

    // Whenever one or more tokens you control enter, this creature becomes prepared.
    triggeredAbility {
        trigger = Triggers.OneOrMorePermanentsEnter(GameObjectFilter.Token)
        effect = Effects.BecomePrepared(EffectTarget.Self)
    }

    // Scrollboost — the prepare spell. One or two target creatures each get +2/+2 until end of turn.
    prepare("Scrollboost") {
        manaCost = "{1}{W}"
        typeLine = "Sorcery"
        oracleText = "One or two target creatures each get +2/+2 until end of turn."
        spell {
            effect = ForEachTargetEffect(
                listOf(Effects.ModifyStats(2, 2, EffectTarget.ContextTarget(0)))
            )
            target = TargetCreature(count = 2, minCount = 1)
        }
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "33"
        artist = "Oriana Menendez"
        imageUri = "https://cards.scryfall.io/normal/front/c/0/c0b85569-2cb3-4b64-b0fe-418195c4dab0.jpg?1775937144"
    }
}
