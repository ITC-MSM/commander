package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.events.SpellCastPredicate
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Danitha, Spear of Agony — "a spell that targets an opponent or a creature an opponent controls":
 * the player half ([SpellCastPredicate.TargetsOpponent]) and the object half
 * ([SpellCastPredicate.TargetsMatching]) joined by [SpellCastPredicate.AnyOf].
 */
val DanithaSpearOfAgony = card("Danitha, Spear of Agony") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Legendary Creature — Human Knight"
    power = 2
    toughness = 2
    oracleText = "First strike\n" +
        "Whenever you cast a spell that targets an opponent or a creature an opponent controls, put " +
        "a +1/+1 counter on Danitha."

    keywords(Keyword.FIRST_STRIKE)

    triggeredAbility {
        trigger = Triggers.youCastSpell(
            requires = setOf(
                SpellCastPredicate.AnyOf(
                    listOf(
                        SpellCastPredicate.TargetsOpponent,
                        SpellCastPredicate.TargetsMatching(GameObjectFilter.Creature.opponentControls()),
                    )
                )
            )
        )
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "227"
        artist = "Bryan Sola"
        flavorText = "\"To know the Cabal is to know despair. None can stand against its legions.\""
        imageUri = "https://cards.scryfall.io/normal/front/6/4/6489814b-3d10-423e-988c-324740d36748.jpg?1789127194"
        inBooster = false
    }
}
