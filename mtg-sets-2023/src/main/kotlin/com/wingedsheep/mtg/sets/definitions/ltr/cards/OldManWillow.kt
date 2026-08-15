package com.wingedsheep.mtg.sets.definitions.ltr.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.ReflexiveTriggerEffect
import com.wingedsheep.sdk.scripting.effects.SacrificeEffect
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Old Man Willow
 * {2}{B}{G}
 * Legendary Creature — Treefolk
 * * / *
 *
 * Old Man Willow's power and toughness are each equal to the number of lands you control.
 * Whenever Old Man Willow attacks, you may sacrifice another creature or a token. When you do,
 * target creature an opponent controls gets -2/-2 until end of turn.
 */
val OldManWillow = card("Old Man Willow") {
    manaCost = "{2}{B}{G}"
    colorIdentity = "BG"
    typeLine = "Legendary Creature — Treefolk"
    oracleText = "Old Man Willow's power and toughness are each equal to the number of lands you control.\n" +
        "Whenever Old Man Willow attacks, you may sacrifice another creature or a token. When you do, target creature an opponent controls gets -2/-2 until end of turn."

    dynamicStats(DynamicAmount.AggregateBattlefield(Player.You, GameObjectFilter.Land))

    triggeredAbility {
        trigger = Triggers.Attacks
        effect = ReflexiveTriggerEffect(
            // "you may sacrifice another creature or a token"
            action = SacrificeEffect(
                filter = GameObjectFilter.Creature.youControl() or GameObjectFilter.Token.youControl(),
                excludeSource = true
            ),
            optional = true,
            // "When you do, target creature an opponent controls gets -2/-2 until end of turn."
            reflexiveEffect = Effects.ModifyStats(-2, -2, EffectTarget.ContextTarget(0)),
            reflexiveTargetRequirements = listOf(Targets.CreatureOpponentControls)
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "217"
        artist = "Miklós Ligeti"
        imageUri = "https://cards.scryfall.io/normal/front/0/3/0362b7c4-fff5-4bc2-b32d-913f85c23cc4.jpg?1686969917"
    }
}
