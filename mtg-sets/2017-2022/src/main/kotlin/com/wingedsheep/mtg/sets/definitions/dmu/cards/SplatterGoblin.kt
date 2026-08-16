package com.wingedsheep.mtg.sets.definitions.dmu.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Splatter Goblin
 * {1}{B}
 * Creature — Phyrexian Goblin
 * 2/1
 * When this creature dies, target creature an opponent controls gets -1/-1 until end of turn.
 */
val SplatterGoblin = card("Splatter Goblin") {
    manaCost = "{1}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Phyrexian Goblin"
    oracleText = "When this creature dies, target creature an opponent controls gets -1/-1 until end of turn."
    power = 2
    toughness = 1

    triggeredAbility {
        trigger = Triggers.Dies
        val t = target("target", TargetCreature(filter = TargetFilter.Creature.opponentControls()))
        effect = Effects.ModifyStats(-1, -1, t)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "109"
        artist = "Lars Grant-West"
        flavorText = "\"For some, perfection is but a single moment when form and purpose converge.\"\n—Rona"
        imageUri = "https://cards.scryfall.io/normal/front/5/0/5045027d-0ff4-484c-b4cb-e0b61885d428.jpg?1783921326"
    }
}
