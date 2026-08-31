package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.DynamicAmount
import com.wingedsheep.sdk.scripting.values.EntityNumericProperty
import com.wingedsheep.sdk.scripting.values.EntityReference

/**
 * Jagged-Scar Archers
 * {1}{G}{G}
 * Creature — Elf Archer
 * Printed power and toughness are both star (a characteristic-defining ability).
 * Jagged-Scar Archers's power and toughness are each equal to the number of Elves you control.
 * {T}: This creature deals damage equal to its power to target creature with flying.
 *
 * The bare noun "Elves" counts every Elf *permanent* you control, not only the creatures — and
 * the Archers is itself an Elf, so it counts itself.
 *
 * The damage amount reads the Archers' power off the *source* at resolution, so it sees the CDA
 * (and anything else in the layer stack) rather than a snapshot taken on activation. Tapping is
 * the whole cost, so the Archers is already tapped when the ability resolves — that doesn't change
 * its power. `damageSource` names the Archers so protection, prevention and "damage dealt by"
 * triggers attribute it correctly.
 */
val JaggedScarArchers = card("Jagged-Scar Archers") {
    manaCost = "{1}{G}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Elf Archer"
    power = 0
    toughness = 0
    oracleText = "Jagged-Scar Archers's power and toughness are each equal to the number of " +
        "Elves you control.\n{T}: This creature deals damage equal to its power to target " +
        "creature with flying."

    dynamicStats(
        DynamicAmounts.battlefield(
            Player.You,
            GameObjectFilter.Permanent.withSubtype(Subtype.ELF)
        ).count()
    )

    activatedAbility {
        cost = Costs.Tap
        val flier = target("target creature with flying", Targets.CreatureWithKeyword(Keyword.FLYING))
        effect = Effects.DealDamage(
            DynamicAmount.EntityProperty(EntityReference.Source, EntityNumericProperty.Power),
            flier,
            damageSource = EffectTarget.Self,
        )
        description = "{T}: This creature deals damage equal to its power to target creature with flying."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "222"
        artist = "Paolo Parente"
        imageUri = "https://cards.scryfall.io/normal/front/7/5/75fd5232-2dac-4bd9-a1f6-eb1a40154367.jpg?1783942861"
    }
}
