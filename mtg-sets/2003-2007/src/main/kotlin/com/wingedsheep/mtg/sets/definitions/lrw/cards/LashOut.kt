package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Lash Out
 * {1}{R}
 * Instant
 * Lash Out deals 3 damage to target creature. Clash with an opponent. If you win, Lash Out deals
 * 3 damage to that creature's controller.
 *
 * Two things worth stating, both of which the scenario test pins:
 *
 *  - **The clash happens whether or not the creature is still there.** The damage and the clash are
 *    separate sentences; only the creature *targeting* can fizzle the spell. Once it resolves, the
 *    clash runs — and per the Broken Ambitions ruling ("the clash happens regardless") that is the
 *    general shape of Lorwyn's clash spells.
 *  - **"That creature's controller" is [EffectTarget.TargetController], not a second target.** It
 *    resolves through the target's controller and falls back to last-known information when the
 *    3 damage already killed it (CR 608.2h), which is the common case: a 3-toughness creature dies
 *    to the first sentence and its controller still takes 3.
 */
val LashOut = card("Lash Out") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Instant"
    oracleText = "Lash Out deals 3 damage to target creature. Clash with an opponent. If you win, " +
        "Lash Out deals 3 damage to that creature's controller."

    spell {
        val creature = target("target creature", Targets.Creature)
        effect = Effects.DealDamage(3, creature)
            .then(Patterns.Mechanic.clash(Effects.DealDamage(3, EffectTarget.TargetController)))
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "183"
        artist = "Scott Hampton"
        imageUri = "https://cards.scryfall.io/normal/front/d/e/de2c0c8b-5442-44fb-9686-d3dff5742501.jpg?1783942872"
    }
}
