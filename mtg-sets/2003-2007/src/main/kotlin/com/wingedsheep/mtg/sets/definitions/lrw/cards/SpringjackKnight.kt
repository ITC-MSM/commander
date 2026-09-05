package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.dsl.Targets

val SpringjackKnight = card("Springjack Knight") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Kithkin Knight"
    power = 2
    toughness = 1
    oracleText = "Whenever this creature attacks, clash with an opponent. If you win, target creature gains double strike until end of turn. (Each clashing player reveals the top card of their library, then puts that card on their choice of the top or bottom. A player wins if their card had a greater mana value.)"

    triggeredAbility {
        trigger = Triggers.Attacks
        val creature = target("creature", Targets.Creature)
        effect = Patterns.Mechanic.clash(Effects.GrantKeyword(Keyword.DOUBLE_STRIKE, creature))
        description = "Clash with an opponent. If you win, target creature gains double strike until end of turn."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "41"
        artist = "Steven Belledin"
        imageUri = "https://cards.scryfall.io/normal/front/7/5/75fa0c72-427b-4797-b5b1-15f07fc5fa07.jpg?1783942908"
    }
}
