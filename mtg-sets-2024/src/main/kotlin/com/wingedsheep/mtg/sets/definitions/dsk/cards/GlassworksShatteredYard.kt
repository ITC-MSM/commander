package com.wingedsheep.mtg.sets.definitions.dsk.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.CardLayout
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Glassworks // Shattered Yard (DSK 137) — split-layout Room (CR 709.5).
 *
 * Glassworks {2}{R} — Enchantment — Room
 *   When you unlock this door, this Room deals 4 damage to target creature an opponent controls.
 *
 * Shattered Yard {4}{R} — Enchantment — Room
 *   At the beginning of your end step, this Room deals 1 damage to each opponent.
 *
 * Cast each half separately; the cast face enters unlocked, the other locked. Pay the locked
 * face's printed mana cost as a sorcery-speed special action to unlock it (CR 709.5e). Both
 * doors use the Room itself as the damage source (the default when no explicit `damageSource`
 * is given to [Effects.DealDamage]).
 */
val GlassworksShatteredYard = card("Glassworks // Shattered Yard") {
    layout = CardLayout.SPLIT
    colorIdentity = "R"

    face("Glassworks") {
        manaCost = "{2}{R}"
        typeLine = "Enchantment — Room"
        oracleText = "When you unlock this door, this Room deals 4 damage to target creature an opponent controls."

        triggeredAbility {
            trigger = Triggers.OnDoorUnlocked
            target = Targets.CreatureOpponentControls
            effect = Effects.DealDamage(4, EffectTarget.ContextTarget(0))
            description = "When you unlock this door, this Room deals 4 damage to target creature an opponent controls."
        }
    }

    face("Shattered Yard") {
        manaCost = "{4}{R}"
        typeLine = "Enchantment — Room"
        oracleText = "At the beginning of your end step, this Room deals 1 damage to each opponent."

        triggeredAbility {
            trigger = Triggers.YourEndStep
            effect = Effects.DealDamage(1, EffectTarget.PlayerRef(Player.EachOpponent))
            description = "At the beginning of your end step, this Room deals 1 damage to each opponent."
        }
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "137"
        artist = "Sergey Glushakov"
        imageUri = "https://cards.scryfall.io/normal/front/f/e/fe32f667-8d9f-4414-913e-256cbc2fbc45.jpg?1726780646"
    }
}
