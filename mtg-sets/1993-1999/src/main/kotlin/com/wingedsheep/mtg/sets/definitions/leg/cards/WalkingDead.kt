package com.wingedsheep.mtg.sets.definitions.leg.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.RegenerateEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Walking Dead
 * {1}{B}
 * Creature — Zombie
 * 1/1
 *
 * {B}: Regenerate this creature.
 */
val WalkingDead = card("Walking Dead") {
    manaCost = "{1}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Zombie"
    power = 1
    toughness = 1
    oracleText = "{B}: Regenerate this creature."

    activatedAbility {
        cost = Costs.Mana("{B}")
        effect = RegenerateEffect(EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "126"
        artist = "Dan Frazier"
        flavorText = "The Walking Dead are the remains of freakish experiments by the Necromantic Lords."
        imageUri = "https://cards.scryfall.io/normal/front/d/7/d7533a72-77d1-40cd-b3a1-7597d566c428.jpg?1783948060"
    }
}
