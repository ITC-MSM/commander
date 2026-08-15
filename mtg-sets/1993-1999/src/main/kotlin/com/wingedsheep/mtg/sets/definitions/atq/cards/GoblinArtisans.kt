package com.wingedsheep.mtg.sets.definitions.atq.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.FlipCoinEffect
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetSpell

/**
 * Goblin Artisans
 * {R}
 * Creature — Goblin Artificer
 * 1/1
 * {T}: Flip a coin. If you win the flip, draw a card. If you lose the flip, counter target artifact
 *   spell you control that isn't the target of an ability from another creature named Goblin
 *   Artisans.
 *
 * The coin-flip win/lose branch ([FlipCoinEffect]) and the targeted counter ([Effects.CounterSpell])
 * compose from existing primitives; the activation targets the artifact spell up front (locked at
 * activation, countered only on a lost flip). The self-referential targeting restriction is the
 * engine gap: `GameObjectFilter.notTargetedByAbilityFromSameNamedSource()`
 * ([com.wingedsheep.sdk.scripting.predicates.StatePredicate.NotTargetedByAbilityFromSameNamedSource])
 * excludes any artifact spell already targeted by an ability on the stack from *another* Goblin
 * Artisans — so two Goblin Artisans can't both lock onto the same spell.
 */
val GoblinArtisans = card("Goblin Artisans") {
    manaCost = "{R}"
    colorIdentity = "R"
    typeLine = "Creature — Goblin Artificer"
    power = 1
    toughness = 1

    oracleText = "{T}: Flip a coin. If you win the flip, draw a card. If you lose the flip, counter " +
        "target artifact spell you control that isn't the target of an ability from another " +
        "creature named Goblin Artisans."

    activatedAbility {
        cost = Costs.Tap
        target(
            "target artifact spell you control",
            TargetSpell(
                filter = TargetFilter(
                    GameObjectFilter.Artifact
                        .youControl()
                        .notTargetedByAbilityFromSameNamedSource(),
                    zone = Zone.STACK
                )
            )
        )
        effect = FlipCoinEffect(
            wonEffect = Effects.DrawCards(1, EffectTarget.PlayerRef(Player.You)),
            lostEffect = Effects.CounterSpell()
        )
        description = "{T}: Flip a coin. If you win the flip, draw a card. If you lose the flip, " +
            "counter target artifact spell you control that isn't the target of an ability from " +
            "another creature named Goblin Artisans."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "26"
        artist = "Julie Baroh"
        imageUri = "https://cards.scryfall.io/normal/front/6/6/6669d96e-9a7b-4427-a477-f4e76831f593.jpg?1562916423"
    }
}
