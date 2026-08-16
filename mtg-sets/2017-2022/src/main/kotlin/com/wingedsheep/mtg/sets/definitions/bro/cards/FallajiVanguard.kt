package com.wingedsheep.mtg.sets.definitions.bro.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding

/**
 * Fallaji Vanguard
 * {2}{R}{W}
 * Creature — Human Soldier
 * 2/3
 * First strike
 * Whenever this creature or another creature you control enters, target creature gets +2/+0 until end of turn.
 *
 * The inclusive "this creature or another creature you control enters" shape is
 * [Triggers.entersBattlefield] over `Creature.youControl()` with [TriggerBinding.ANY] — the same
 * construct Elrond, Lord of Rivendell uses. `ANY` is what lets the source itself entering fire it.
 */
val FallajiVanguard = card("Fallaji Vanguard") {
    manaCost = "{2}{R}{W}"
    colorIdentity = "WR"
    typeLine = "Creature — Human Soldier"
    power = 2
    toughness = 3
    oracleText = "First strike\n" +
        "Whenever this creature or another creature you control enters, target creature gets +2/+0 until end of turn."

    keywords(Keyword.FIRST_STRIKE)

    triggeredAbility {
        trigger = Triggers.entersBattlefield(
            filter = GameObjectFilter.Creature.youControl(),
            binding = TriggerBinding.ANY
        )
        val creature = target("target creature", Targets.Creature)
        effect = Effects.ModifyStats(2, 0, creature)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "210"
        artist = "Joshua Cairos"
        flavorText = "\"The Burnished Banner will show the Warlord that the Suwwardi Marches belong to the Fallaji!\""
        imageUri = "https://cards.scryfall.io/normal/front/f/f/ffe9ee1c-5eb3-4d63-a641-0ec5adf7b058.jpg?1783920030"
    }
}
