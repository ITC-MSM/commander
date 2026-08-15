package com.wingedsheep.mtg.sets.definitions.tla.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Suki, Courageous Rescuer
 * {1}{W}{W}
 * Legendary Creature — Human Warrior Ally
 * 2/4
 *
 * Other creatures you control get +1/+0.
 * Whenever another permanent you control leaves the battlefield during your turn, create a 1/1
 * white Ally creature token. This ability triggers only once each turn.
 *
 * The anthem is a static [ModifyStats] over [GroupFilter] creatures-you-control with
 * `excludeSelf` (so Suki doesn't pump herself). The leave-the-battlefield trigger reuses the
 * generic [Triggers.leavesBattlefield] factory with an `OTHER` binding ("another permanent you
 * control"), gated to your turn via [Conditions.IsYourTurn] and limited to a single firing per
 * turn with `oncePerTurn` (same shell as Moonstone Harbinger).
 */
val SukiCourageousRescuer = card("Suki, Courageous Rescuer") {
    manaCost = "{1}{W}{W}"
    colorIdentity = "W"
    typeLine = "Legendary Creature — Human Warrior Ally"
    power = 2
    toughness = 4
    oracleText = "Other creatures you control get +1/+0.\n" +
        "Whenever another permanent you control leaves the battlefield during your turn, create a " +
        "1/1 white Ally creature token. This ability triggers only once each turn."

    // Other creatures you control get +1/+0.
    staticAbility {
        ability = ModifyStats(
            powerBonus = 1,
            toughnessBonus = 0,
            filter = GroupFilter(GameObjectFilter.Creature.youControl(), excludeSelf = true)
        )
    }

    // Whenever another permanent you control leaves the battlefield during your turn, create a
    // 1/1 white Ally creature token. This ability triggers only once each turn.
    triggeredAbility {
        trigger = Triggers.leavesBattlefield(
            filter = GameObjectFilter.Permanent.youControl(),
            binding = TriggerBinding.OTHER,
        )
        triggerCondition = Conditions.IsYourTurn
        oncePerTurn = true
        effect = Effects.CreateToken(
            power = 1,
            toughness = 1,
            colors = setOf(Color.WHITE),
            creatureTypes = setOf("Ally")
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "37"
        artist = "Tky"
        flavorText = "\"I'm okay. Just finish the mission!\""
        imageUri = "https://cards.scryfall.io/normal/front/3/3/33b97433-e16c-422e-a0ca-f6b1e98d8681.jpg?1764120140"
    }
}
