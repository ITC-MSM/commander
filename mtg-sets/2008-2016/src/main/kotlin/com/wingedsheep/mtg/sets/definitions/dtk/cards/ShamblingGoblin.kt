package com.wingedsheep.mtg.sets.definitions.dtk.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Shambling Goblin
 * {B}
 * Creature — Zombie Goblin
 * 1/1
 *
 * When this creature dies, target creature an opponent controls gets -1/-1 until end of turn.
 */
val ShamblingGoblin = card("Shambling Goblin") {
    manaCost = "{B}"
    colorIdentity = "B"
    typeLine = "Creature — Zombie Goblin"
    oracleText = "When this creature dies, target creature an opponent controls gets -1/-1 until end of turn."
    power = 1
    toughness = 1

    triggeredAbility {
        trigger = Triggers.Dies
        val creature = target("target creature an opponent controls", Targets.CreatureOpponentControls)
        effect = Effects.ModifyStats(-1, -1, creature)
        description = "When this creature dies, target creature an opponent controls gets -1/-1 until end of turn."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "118"
        artist = "Yeong-Hao Han"
        flavorText = "\"The Kolaghan send them at us. We kill and raise them. They fight the next wave the Kolaghan send. It's a neat little cycle.\"\n—Asmala, Silumgar sorcerer"
        imageUri = "https://cards.scryfall.io/normal/front/9/4/9404c7d5-3450-4425-94e5-aa7d4e571d4e.jpg?1783938594"
    }
}
