package com.wingedsheep.mtg.sets.definitions.akh.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Ornery Kudu
 * {2}{G}
 * Creature — Antelope
 * 3/4
 * When this creature enters, put a -1/-1 counter on target creature you control.
 */
val OrneryKudu = card("Ornery Kudu") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Antelope"
    oracleText = "When this creature enters, put a -1/-1 counter on target creature you control."
    power = 3
    toughness = 4

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val creature = target("target creature you control", Targets.CreatureYouControl)
        effect = Effects.AddCounters(Counters.MINUS_ONE_MINUS_ONE, 1, creature)
        description = "When this creature enters, put a -1/-1 counter on target creature you control."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "178"
        artist = "Deruchenko Alexander"
        flavorText = "Debate rages among the viziers whether comparing the kudu's horns to the God-Pharaoh's is blasphemy or reverence."
        imageUri = "https://cards.scryfall.io/normal/front/8/b/8ba1d3a1-8c1b-4b77-b149-13d5ad9f125a.jpg?1783936471"
    }
}
