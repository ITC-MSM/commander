package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.MustBeBlocked
import com.wingedsheep.sdk.scripting.targets.EffectTarget

val NathsElite = card("Nath's Elite") {
    manaCost = "{4}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Elf Warrior"
    power = 4
    toughness = 2
    oracleText = "All creatures able to block this creature do so.\nWhen this creature enters, clash with an opponent. If you win, put a +1/+1 counter on this creature. (Each clashing player reveals the top card of their library, then puts that card on their choice of the top or bottom. A player wins if their card had a greater mana value.)"

    staticAbility {
        ability = MustBeBlocked(allCreatures = true)
    }

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Patterns.Mechanic.clash(
            Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.Self)
        )
        description = "Clash with an opponent. If you win, put a +1/+1 counter on this creature."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "231"
        artist = "Wayne Reynolds"
        imageUri = "https://cards.scryfall.io/normal/front/f/2/f2a2b029-02ba-46b0-88a2-99025727cc56.jpg?1783942859"
    }
}
