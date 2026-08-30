package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.AnyTarget
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Char
 * {2}{R}
 * Instant
 *
 * Char deals 4 damage to any target and 2 damage to you.
 *
 * The self-damage is not targeted, so it happens even when the chosen target has become
 * illegal — the spell only fizzles if its single target is gone, in which case nothing at
 * all resolves (CR 608.2b).
 */
val Char = card("Char") {
    manaCost = "{2}{R}"
    colorIdentity = "R"
    typeLine = "Instant"
    oracleText = "Char deals 4 damage to any target and 2 damage to you."

    spell {
        val victim = target("any target", AnyTarget())
        effect = Effects.DealDamage(4, victim) then
            Effects.DealDamage(2, EffectTarget.PlayerRef(Player.You))
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "117"
        artist = "Adam Rex"
        flavorText = "Izzet mages often acquire their magic reagents from dubious sources, so the potency of their spells is never predictable."
        imageUri = "https://cards.scryfall.io/normal/front/f/f/ff3a24af-e995-4d05-ac2c-e9676048675d.jpg?1783943658"
    }
}
