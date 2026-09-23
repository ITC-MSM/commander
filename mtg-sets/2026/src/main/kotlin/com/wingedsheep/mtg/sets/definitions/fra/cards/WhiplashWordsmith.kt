package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ConditionalStaticAbility
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

val WhiplashWordsmith = card("Whiplash Wordsmith") {
    manaCost = "{3}{B/R}"
    colorIdentity = "BR"
    typeLine = "Creature — Vampire Sorcerer"
    power = 3
    toughness = 3
    oracleText = "This creature enters prepared. (While it's prepared, you may cast a copy of its spell. Doing so unprepares it.)\n" +
        "As long as an opponent was dealt noncombat damage this turn, this creature has flying and haste."

    keywords(Keyword.PREPARED)

    staticAbility {
        ability = ConditionalStaticAbility(
            ability = GrantKeyword(Keyword.FLYING, GroupFilter.source()),
            condition = Conditions.OpponentWasDealtNoncombatDamageThisTurn
        )
    }

    staticAbility {
        ability = ConditionalStaticAbility(
            ability = GrantKeyword(Keyword.HASTE, GroupFilter.source()),
            condition = Conditions.OpponentWasDealtNoncombatDamageThisTurn
        )
    }

    prepare("Vicious Verse") {
        manaCost = "{B/R}"
        typeLine = "Sorcery"
        oracleText = "Vicious Verse deals 1 damage to target opponent."
        spell {
            val opponent = target("target opponent", Targets.Opponent)
            effect = Effects.DealDamage(1, opponent)
        }
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "164"
        artist = "Anastasia Ovchinnikova"
        imageUri = "https://cards.scryfall.io/normal/front/8/0/8096bc9a-a610-448f-bef2-7230e17e9777.jpg?1789127692"
        inBooster = false
    }
}
