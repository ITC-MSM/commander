package com.wingedsheep.mtg.sets.definitions.avr.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Vigilante Justice
 * {3}{R}
 * Enchantment
 *
 * Whenever a Human you control enters, this enchantment deals 1 damage to any target.
 *
 * "A Human you control" is a bare tribal noun with no "creature" beside it, so the filter is
 * [GameObjectFilter.Permanent] with the subtype — a Human artifact or a Human enchantment creature
 * token both count. The binding is ANY rather than OTHER because the printed text says "a Human",
 * not "another Human"; the enchantment itself can never be the permanent that entered, so the two
 * differ only in principle here, but the principle is what the wording states.
 */
val VigilanteJustice = card("Vigilante Justice") {
    manaCost = "{3}{R}"
    colorIdentity = "R"
    typeLine = "Enchantment"
    oracleText = "Whenever a Human you control enters, this enchantment deals 1 damage to any target."

    triggeredAbility {
        trigger = Triggers.entersBattlefield(
            filter = GameObjectFilter.Permanent.withSubtype("Human").youControl(),
            binding = TriggerBinding.ANY
        )
        target = Targets.Any
        effect = Effects.DealDamage(1, EffectTarget.ContextTarget(0))
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "165"
        artist = "Steve Prescott"
        flavorText = "It begins as a whisper and ends with the red roar of fire."
        imageUri = "https://cards.scryfall.io/normal/front/a/9/a9db329b-6248-4082-bfc8-5d2c0db43338.jpg?1783940673"
    }
}
