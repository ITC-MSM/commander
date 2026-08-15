package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.ForEachTargetEffect
import com.wingedsheep.sdk.scripting.effects.MayPlayExpiry
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Opera Love Song
 * {1}{R}
 * Instant
 * Choose one —
 * • Exile the top two cards of your library. You may play those cards until your next end step.
 * • One or two target creatures each get +2/+0 until end of turn.
 *
 * Mode 1 is impulse draw: [Patterns.Exile.impulse] gathers the top two cards into exile and grants a
 * play permission. The window is "until your next end step" — [MayPlayExpiry.UntilNextEndStep], which
 * (unlike the default end-of-turn expiry) keeps the cards playable through the end step of *this* turn
 * when cast on your own turn and otherwise lasts into your next turn.
 *
 * Mode 2 is "one or two target" — a single requirement with min 1 / max 2 targets locked at cast
 * (`TargetObject(count = 2, minCount = 1)`), then [ForEachTargetEffect] applies +2/+0 until end of
 * turn to each chosen creature.
 */
val OperaLoveSong = card("Opera Love Song") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Instant"
    oracleText = "Choose one —\n" +
        "• Exile the top two cards of your library. You may play those cards until your next end step.\n" +
        "• One or two target creatures each get +2/+0 until end of turn."

    spell {
        modal(chooseCount = 1) {
            mode("Exile the top two cards of your library. You may play those cards until your next end step.") {
                effect = Patterns.Exile.impulse(count = 2, expiry = MayPlayExpiry.UntilNextEndStep)
            }
            mode("One or two target creatures each get +2/+0 until end of turn.") {
                target = TargetObject(
                    count = 2,
                    minCount = 1,
                    filter = TargetFilter(GameObjectFilter.Creature)
                )
                effect = ForEachTargetEffect(
                    effects = listOf(Effects.ModifyStats(2, 0, EffectTarget.ContextTarget(0)))
                )
            }
        }
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "147"
        artist = "Grace Zhu"
        flavorText = "\"O my hero, my beloved, shall we still be made to part?\""
        imageUri = "https://cards.scryfall.io/normal/front/0/3/0343916d-1b65-4e95-aef1-e72dbcebf0c4.jpg?1748706312"
    }
}
