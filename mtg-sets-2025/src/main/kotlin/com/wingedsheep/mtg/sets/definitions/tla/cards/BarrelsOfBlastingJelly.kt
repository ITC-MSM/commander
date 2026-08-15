package com.wingedsheep.mtg.sets.definitions.tla.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ActivationRestriction

/**
 * Barrels of Blasting Jelly
 * {1}
 * Artifact
 * {1}: Add one mana of any color. Activate only once each turn.
 * {5}, {T}, Sacrifice this artifact: It deals 5 damage to target creature.
 *
 * The first ability is a once-per-turn ([ActivationRestriction.OncePerTurn]) any-color mana
 * ability ([Effects.AddAnyColorMana]). The second is a sacrifice-self activated ability that
 * deals 5 damage to a target creature; the artifact is the damage source ("It deals"), which is
 * the default for [Effects.DealDamage] when no explicit damageSource is given.
 */
val BarrelsOfBlastingJelly = card("Barrels of Blasting Jelly") {
    manaCost = "{1}"
    colorIdentity = ""
    typeLine = "Artifact"
    oracleText = "{1}: Add one mana of any color. Activate only once each turn.\n" +
        "{5}, {T}, Sacrifice this artifact: It deals 5 damage to target creature."

    activatedAbility {
        cost = Costs.Mana("{1}")
        effect = Effects.AddAnyColorMana(1)
        manaAbility = true
        restrictions = listOf(ActivationRestriction.OncePerTurn)
    }

    activatedAbility {
        cost = Costs.Composite(
            Costs.Mana("{5}"),
            Costs.Tap,
            Costs.SacrificeSelf,
        )
        val t = target("target", Targets.Creature)
        effect = Effects.DealDamage(5, t)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "254"
        artist = "Salvatorre Zee Yazzie"
        flavorText = "Not to be confused with barrels of jelly candy."
        imageUri = "https://cards.scryfall.io/normal/front/4/f/4fd83083-3779-41f0-90a0-a00e3270e4e6.jpg?1764121877"
    }
}
