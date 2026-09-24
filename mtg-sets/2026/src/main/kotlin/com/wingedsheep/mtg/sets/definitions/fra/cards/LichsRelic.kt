package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.effects.ForEachTargetEffect
import com.wingedsheep.sdk.scripting.effects.PayManaCostEffect
import com.wingedsheep.sdk.scripting.effects.ReflexiveTriggerEffect
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetObject
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Lich's Relic — Reality Fracture #57
 * {B} · Artifact — Equipment · Rare
 *
 * When this Equipment enters, you may pay {2}. When you do, for each opponent, destroy up to one
 * target creature or planeswalker that player controls.
 * Equipped creature gets +2/+1.
 * Equip {2}
 *
 * The enters trigger is a "When you do" reflexive (CR 603.12): the optional {2} is the action, and
 * the reflexive ability's targets are chosen only once it has been paid. Its targets are Kaya,
 * Spirits' Justice's one-per-player distribution — up to one creature or planeswalker per opponent
 * (`dynamicMaxCount = PlayerCount(EachOpponent)` + `differentControllers = true`, `optional` for
 * the "up to") — and each chosen permanent is then destroyed.
 */
val LichsRelic = card("Lich's Relic") {
    manaCost = "{B}"
    colorIdentity = "B"
    typeLine = "Artifact — Equipment"
    oracleText = "When this Equipment enters, you may pay {2}. When you do, for each opponent, destroy up " +
        "to one target creature or planeswalker that player controls.\n" +
        "Equipped creature gets +2/+1.\n" +
        "Equip {2}"

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = ReflexiveTriggerEffect(
            action = PayManaCostEffect(ManaCost.parse("{2}")),
            optional = true,
            reflexiveEffect = ForEachTargetEffect(
                listOf(Effects.Destroy(EffectTarget.ContextTarget(0)))
            ),
            reflexiveTargetRequirements = listOf(
                TargetObject(
                    filter = TargetFilter(GameObjectFilter.CreatureOrPlaneswalker.opponentControls()),
                    optional = true,
                    dynamicMaxCount = DynamicAmount.PlayerCount(Player.EachOpponent),
                    differentControllers = true,
                    id = "up to one target creature or planeswalker each opponent controls",
                )
            ),
            descriptionOverride = "you may pay {2}. When you do, for each opponent, destroy up to one " +
                "target creature or planeswalker that player controls"
        )
        description = "When this Equipment enters, you may pay {2}. When you do, for each opponent, " +
            "destroy up to one target creature or planeswalker that player controls."
    }

    staticAbility {
        ability = ModifyStats(+2, +1, Filters.EquippedCreature)
    }

    equipAbility("{2}")

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "57"
        artist = "Gaboleps"
        imageUri = "https://cards.scryfall.io/normal/front/b/1/b105511d-5022-4a84-b6ce-4bb433e93a62.jpg?1789644819"
        inBooster = false
    }
}
