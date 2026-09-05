package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CantBlock
import com.wingedsheep.sdk.scripting.targets.EffectTarget

val BogHoodlums = card("Bog Hoodlums") {
    manaCost = "{5}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Goblin Warrior"
    power = 4
    toughness = 1
    oracleText = "This creature can't block.\nWhen this creature enters, clash with an opponent. If you win, put a +1/+1 counter on this creature. (Each clashing player reveals the top card of their library, then puts that card on their choice of the top or bottom. A player wins if their card had a greater mana value.)"

    staticAbility {
        ability = CantBlock()
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
        collectorNumber = "100"
        artist = "Brandon Dorman"
        imageUri = "https://cards.scryfall.io/normal/front/f/2/f2459953-a4b5-4a9c-85ed-928b684d7240.jpg?1783942894"
    }
}
