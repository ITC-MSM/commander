package com.wingedsheep.mtg.sets.definitions.mkm.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.MustBeBlockedEffect
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Anzrag, the Quake-Mole — Murders at Karlov Manor #186
 * {2}{R}{G} · Legendary Creature — Mole God · 8/4
 *
 * Whenever Anzrag becomes blocked, untap each creature you control. After this phase, there is an
 * additional combat phase.
 * {3}{R}{R}{G}{G}: Anzrag must be blocked each combat this turn if able.
 *
 * The two halves are a loop by design: pay the activation, Anzrag is forced through a block, the
 * becomes-blocked trigger untaps the team and queues another combat, and the block requirement is
 * still there for that combat too. That last part is why the activated ability uses
 * [MustBeBlockedEffect] rather than a one-combat requirement — `MustBeBlockedExecutor` installs a
 * `Duration.EndOfTurn` floating effect, so the requirement outlives the combat phase it was
 * activated in and applies again in each additional one, exactly as "each combat this turn" prints.
 *
 * `allCreatures = false` is the Gaea's-Protector reading, not the Lure reading: the printed text is
 * "must be blocked … if able" (at least one blocker), not "all creatures able to block it do so".
 *
 * The untap is `ForEachInGroup` over creatures you control rather than a targeted untap — the
 * trigger targets nothing, so it can't be fizzled and doesn't care about hexproof.
 * [Effects.AddCombatPhase] is the atomic extra-combat piece with no trailing main phase, which is
 * what "After this phase, there is an additional combat phase" prints (CR 500.8).
 */
val AnzragTheQuakeMole = card("Anzrag, the Quake-Mole") {
    manaCost = "{2}{R}{G}"
    colorIdentity = "RG"
    typeLine = "Legendary Creature — Mole God"
    oracleText = "Whenever Anzrag becomes blocked, untap each creature you control. After this " +
        "phase, there is an additional combat phase.\n" +
        "{3}{R}{R}{G}{G}: Anzrag must be blocked each combat this turn if able."
    power = 8
    toughness = 4

    triggeredAbility {
        trigger = Triggers.BecomesBlocked
        effect = Effects.Composite(
            Effects.ForEachInGroup(
                GroupFilter(GameObjectFilter.Creature.youControl()),
                Effects.Untap(EffectTarget.Self),
            ),
            Effects.AddCombatPhase,
        )
        description = "Whenever Anzrag becomes blocked, untap each creature you control. After " +
            "this phase, there is an additional combat phase."
    }

    activatedAbility {
        cost = Costs.Mana("{3}{R}{R}{G}{G}")
        effect = MustBeBlockedEffect(EffectTarget.Self, allCreatures = false)
        description = "Anzrag must be blocked each combat this turn if able."
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "186"
        artist = "Helge C. Balzer"
        flavorText = "His claws tear at the foundations of civilization."
        imageUri = "https://cards.scryfall.io/normal/front/7/0/70e9d8b8-4b32-4414-b32f-1f47523239c5.jpg?1783912855"
    }
}
