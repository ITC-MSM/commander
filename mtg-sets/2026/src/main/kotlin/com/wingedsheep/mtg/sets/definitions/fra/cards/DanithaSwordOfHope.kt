package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.events.SpellCastPredicate

/**
 * Danitha, Sword of Hope — "an Equipment spell or a spell that targets a creature you control" is a
 * disjunction over two different kinds of fact (what the spell *is*, what it *targets*), so it is one
 * [SpellCastPredicate.AnyOf] rather than a `spellFilter`. A spell that is both counts once.
 */
val DanithaSwordOfHope = card("Danitha, Sword of Hope") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Legendary Creature — Human Knight"
    power = 2
    toughness = 2
    oracleText = "First strike\n" +
        "Whenever you cast an Equipment spell or a spell that targets a creature you control, draw " +
        "a card. This ability triggers only once each turn."

    keywords(Keyword.FIRST_STRIKE)

    triggeredAbility {
        trigger = Triggers.youCastSpell(
            requires = setOf(
                SpellCastPredicate.AnyOf(
                    listOf(
                        SpellCastPredicate.SpellMatches(GameObjectFilter.Any.withSubtype(Subtype.EQUIPMENT)),
                        SpellCastPredicate.TargetsMatching(GameObjectFilter.Creature.youControl()),
                    )
                )
            )
        )
        oncePerTurn = true
        effect = Effects.DrawCards(1)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "196"
        artist = "Bryan Sola"
        flavorText = "\"Benalia stands for hope and light. I pledge my blade to any that fall under its banner.\""
        imageUri = "https://cards.scryfall.io/normal/front/d/5/d5cb9810-3c2d-4ae4-b8b6-0155ca3f47ad.jpg?1789127178"
        inBooster = false
    }
}
