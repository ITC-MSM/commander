package com.wingedsheep.mtg.sets.definitions.inv.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.AdditionalManaOnSourceTap
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.DealDamageEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Overabundance
 * {1}{R}{G}
 * Enchantment
 * Whenever a player taps a land for mana, that player adds one mana of any type that land produced,
 * and this enchantment deals 1 damage to the player.
 *
 * Invasion engine gap #3. Built from the existing [AdditionalManaOnSourceTap] tap-bonus static:
 * `color = null` mirrors the produced mana ("any type that land produced"), and the new `rider`
 * resolves the "deals 1 damage to the player" clause inline (controlled by the tapping player, so
 * `EffectTarget.Controller` is the tapper and `EffectTarget.Self` is this enchantment).
 *
 * Like all mana-ability side effects in the engine, the mirror mana and the damage apply when a
 * player manually taps a land; automatic cost payment adds the mirror mana via the mana solver but
 * skips the damage rider (the same limitation that applies to e.g. City of Brass during auto-pay).
 */
val Overabundance = card("Overabundance") {
    manaCost = "{1}{R}{G}"
    colorIdentity = "RG"
    typeLine = "Enchantment"
    oracleText = "Whenever a player taps a land for mana, that player adds one mana of any type that land produced, and this enchantment deals 1 damage to the player."

    staticAbility {
        ability = AdditionalManaOnSourceTap(
            sourceFilter = GameObjectFilter.Land,
            color = null, // mirror the produced color ("any type that land produced")
            rider = DealDamageEffect(1, EffectTarget.Controller, damageSource = EffectTarget.Self)
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "259"
        artist = "Ben Thompson"
        imageUri = "https://cards.scryfall.io/normal/front/4/1/4183e73d-609a-4292-b173-e39eb51949f3.jpg?1562908150"
    }
}
