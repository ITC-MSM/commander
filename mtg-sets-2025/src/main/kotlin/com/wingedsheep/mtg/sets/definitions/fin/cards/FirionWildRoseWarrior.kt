package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.ReduceEquipCost
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Firion, Wild Rose Warrior
 * {2}{R}
 * Legendary Creature — Human Rebel Warrior
 * 3/3
 * Equipped creatures you control have haste.
 * Whenever a nontoken Equipment you control enters, create a token that's a copy of it, except it
 * has "This Equipment's equip abilities cost {2} less to activate." Sacrifice that token at the
 * beginning of the next upkeep.
 *
 * First ability: a continuous [GrantKeyword] of haste over the equipped creatures you control
 * (`GameObjectFilter.Creature.youControl().equipped()`), projected each turn.
 *
 * Second ability: a [Triggers.entersBattlefield] trigger over nontoken Equipment you control
 * (`.nontoken()` also stops the created token — itself an Equipment — from re-triggering, so no
 * loop). It copies the Equipment that entered ([EffectTarget.TriggeringEntity]) via
 * [Effects.CreateTokenCopyOfTarget], granting the copy the "except it has …" clause as an added
 * [ReduceEquipCost] with `onlyOwnEquip = true` (the discount applies only to that token's own
 * equip abilities, not every Equipment you control), and schedules the delayed sacrifice for the
 * next upkeep ([Step.UPKEEP] — the very next upkeep of any player, not gated to your turn).
 */
val FirionWildRoseWarrior = card("Firion, Wild Rose Warrior") {
    manaCost = "{2}{R}"
    colorIdentity = "R"
    typeLine = "Legendary Creature — Human Rebel Warrior"
    power = 3
    toughness = 3
    oracleText = "Equipped creatures you control have haste.\n" +
        "Whenever a nontoken Equipment you control enters, create a token that's a copy of it, " +
        "except it has \"This Equipment's equip abilities cost {2} less to activate.\" Sacrifice " +
        "that token at the beginning of the next upkeep."

    // Equipped creatures you control have haste.
    staticAbility {
        ability = GrantKeyword(
            Keyword.HASTE,
            GroupFilter(GameObjectFilter.Creature.youControl().equipped()),
        )
    }

    // Whenever a nontoken Equipment you control enters, create a sacrificing token copy of it.
    triggeredAbility {
        trigger = Triggers.entersBattlefield(
            filter = GameObjectFilter.Artifact.withSubtype("Equipment").youControl().nontoken(),
            binding = TriggerBinding.ANY,
        )
        effect = Effects.CreateTokenCopyOfTarget(
            target = EffectTarget.TriggeringEntity,
            addedStaticAbilities = listOf(ReduceEquipCost(amount = 2, onlyOwnEquip = true)),
            sacrificeAtStep = Step.UPKEEP,
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "137"
        artist = "Elizabeth Peiró"
        imageUri = "https://cards.scryfall.io/normal/front/9/8/98366937-d15b-4a66-b9f6-878d50b63871.jpg?1782686498"
    }
}
