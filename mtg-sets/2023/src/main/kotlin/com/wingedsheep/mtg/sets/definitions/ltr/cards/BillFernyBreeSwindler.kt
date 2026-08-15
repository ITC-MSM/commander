package com.wingedsheep.mtg.sets.definitions.ltr.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.GiveControlToTargetPlayerEffect
import com.wingedsheep.sdk.scripting.effects.Mode
import com.wingedsheep.sdk.scripting.effects.ModalEffect
import com.wingedsheep.sdk.scripting.effects.RemoveFromCombatEffect
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Bill Ferny, Bree Swindler
 * {1}{U}
 * Legendary Creature — Human Rogue
 * 2/1
 * Whenever Bill Ferny becomes blocked, choose one —
 * • Create a Treasure token.
 * • Target opponent gains control of target Horse you control. If they do, remove Bill Ferny
 *   from combat and create three Treasure tokens.
 */
val BillFernyBreeSwindler = card("Bill Ferny, Bree Swindler") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Legendary Creature — Human Rogue"
    power = 2
    toughness = 1
    oracleText = "Whenever Bill Ferny becomes blocked, choose one —\n" +
        "• Create a Treasure token. (It's an artifact with \"{T}, Sacrifice this token: Add one mana of any color.\")\n" +
        "• Target opponent gains control of target Horse you control. If they do, remove Bill Ferny from combat and create three Treasure tokens."

    triggeredAbility {
        trigger = Triggers.BecomesBlocked
        effect = ModalEffect.chooseOne(
            // Mode 1: Create a Treasure token.
            Mode.noTarget(
                Effects.CreateTreasure(1),
                "Create a Treasure token"
            ),
            // Mode 2: Target opponent gains control of target Horse you control.
            Mode(
                effect = GiveControlToTargetPlayerEffect(
                    permanent = EffectTarget.ContextTarget(1),
                    newController = EffectTarget.ContextTarget(0)
                )
                    .then(RemoveFromCombatEffect(EffectTarget.Self))
                    .then(Effects.CreateTreasure(3)),
                targetRequirements = listOf(
                    Targets.Opponent,
                    TargetCreature(
                        filter = TargetFilter(GameObjectFilter.Creature.withSubtype("Horse").youControl())
                    )
                ),
                description = "Target opponent gains control of target Horse you control. If they do, remove Bill Ferny from combat and create three Treasure tokens"
            )
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "42"
        artist = "Hristo D. Chukov"
        imageUri = "https://cards.scryfall.io/normal/front/2/0/20ac63cb-fa4d-4340-8062-1029c8bd5ec8.jpg?1686968020"
    }
}
