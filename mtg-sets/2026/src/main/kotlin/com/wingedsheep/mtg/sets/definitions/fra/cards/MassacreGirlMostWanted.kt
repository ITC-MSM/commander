package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.events.DamageType
import com.wingedsheep.sdk.scripting.events.RecipientFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * The two abilities feed each other: the 1 damage from the first is noncombat damage to an
 * opponent, so it grows Massacre Girl through the second. The damage trigger watches every source,
 * not just Massacre Girl.
 */
val MassacreGirlMostWanted = card("Massacre Girl, Most Wanted") {
    manaCost = "{4}{B}"
    colorIdentity = "B"
    typeLine = "Legendary Creature — Human Assassin"
    power = 4
    toughness = 4
    oracleText = "Whenever another creature or planeswalker you control dies, Massacre Girl deals 1 damage " +
        "to target opponent and you gain 1 life.\n" +
        "Whenever an opponent is dealt noncombat damage, put a +1/+1 counter on Massacre Girl."

    triggeredAbility {
        trigger = Triggers.leavesBattlefield(
            filter = GameObjectFilter.CreatureOrPlaneswalker.youControl(),
            to = Zone.GRAVEYARD,
            binding = TriggerBinding.OTHER
        )
        val opponent = target("target opponent", Targets.Opponent)
        effect = Effects.Composite(
            Effects.DealDamage(1, opponent),
            Effects.GainLife(1)
        )
    }

    triggeredAbility {
        trigger = Triggers.dealsDamage(
            damageType = DamageType.NonCombat,
            recipient = RecipientFilter.Opponent,
            binding = TriggerBinding.ANY,
        )
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.Self)
        description = "Whenever an opponent is dealt noncombat damage, put a +1/+1 counter on Massacre Girl."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "234"
        artist = "Cristi Balanescu"
        flavorText = "Ravnica's most feared—and most adored—serial killer."
        imageUri = "https://cards.scryfall.io/normal/front/9/0/9028d31f-9c41-47e3-885b-6a869bca8178.jpg?1789644882"
        inBooster = false
    }
}
