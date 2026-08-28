package com.wingedsheep.mtg.sets.definitions.p02.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Goblin General
 * {1}{R}{R}
 * Creature — Goblin Warrior
 *
 * "Goblin creatures you control" is the group iteration of [Effects.ForEachInGroup] — the pump is
 * written once against [EffectTarget.Self], the current iteration entity, exactly as
 * Rally the Troops writes its untap.
 */
val GoblinGeneral = card("Goblin General") {
    manaCost = "{1}{R}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Goblin Warrior"
    oracleText = "Whenever this creature attacks, Goblin creatures you control get +1/+1 until end of turn."
    power = 1
    toughness = 1

    triggeredAbility {
        trigger = Triggers.Attacks
        effect = Effects.ForEachInGroup(
            GroupFilter(GameObjectFilter.Creature.withSubtype(Subtype.GOBLIN).youControl()),
            Effects.ModifyStats(1, 1, EffectTarget.Self)
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "97"
        artist = "Keith Parkinson"
        flavorText = "Lead, follow, or run around like crazy."
        imageUri = "https://cards.scryfall.io/normal/front/7/f/7fdce2e5-60dd-4993-b74c-f49f013b28f0.jpg"
    }
}
