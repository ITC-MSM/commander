package com.wingedsheep.mtg.sets.definitions.lgn.cards

import com.wingedsheep.sdk.core.AbilityFlag
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.AbilityCost
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Sunstrike Legionnaire
 * {1}{W}
 * Creature — Human Soldier
 * 1/2
 * Sunstrike Legionnaire doesn't untap during your untap step.
 * Whenever another creature enters the battlefield, untap Sunstrike Legionnaire.
 * {T}: Tap target creature with mana value 3 or less.
 */
val SunstrikeLegionnaire = card("Sunstrike Legionnaire") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Soldier"
    power = 1
    toughness = 2
    oracleText = "Sunstrike Legionnaire doesn't untap during your untap step.\nWhenever another creature enters the battlefield, untap Sunstrike Legionnaire.\n{T}: Tap target creature with mana value 3 or less."

    flags(AbilityFlag.DOESNT_UNTAP)

    triggeredAbility {
        trigger = Triggers.entersBattlefield(
            filter = GameObjectFilter.Creature,
            binding = TriggerBinding.OTHER,
        )
        effect = Effects.Untap(EffectTarget.Self)
    }

    activatedAbility {
        cost = AbilityCost.Tap
        val t = target("target", TargetCreature(filter = TargetFilter.Creature.manaValueAtMost(3)))
        effect = Effects.Tap(t)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "22"
        artist = "Mark Zug"
        imageUri = "https://cards.scryfall.io/normal/front/0/f/0f5d519a-9f11-4b10-97ad-edccfda639bb.jpg?1562898137"
    }
}
