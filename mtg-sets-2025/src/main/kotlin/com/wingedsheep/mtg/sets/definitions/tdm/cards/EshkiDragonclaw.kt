package com.wingedsheep.mtg.sets.definitions.tdm.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.effects.WardCost
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Eshki Dragonclaw — Tarkir: Dragonstorm #182
 * {1}{G}{U}{R} · Legendary Creature — Human Warrior · 4/4
 *
 * Vigilance, trample, ward {1}
 * At the beginning of combat on your turn, if you've cast both a creature spell and a
 * noncreature spell this turn, draw a card and put two +1/+1 counters on Eshki Dragonclaw.
 */
val EshkiDragonclaw = card("Eshki Dragonclaw") {
    manaCost = "{1}{G}{U}{R}"
    colorIdentity = "GUR"
    typeLine = "Legendary Creature — Human Warrior"
    power = 4
    toughness = 4
    oracleText = "Vigilance, trample, ward {1}\n" +
        "At the beginning of combat on your turn, if you've cast both a creature spell and a " +
        "noncreature spell this turn, draw a card and put two +1/+1 counters on Eshki Dragonclaw."

    keywords(Keyword.VIGILANCE, Keyword.TRAMPLE)
    keywordAbility(KeywordAbility.Ward(WardCost.Mana("{1}")))

    triggeredAbility {
        trigger = Triggers.BeginCombat
        triggerCondition = Conditions.All(
            Conditions.YouCastSpellsThisTurn(atLeast = 1, filter = GameObjectFilter.Creature),
            Conditions.YouCastSpellsThisTurn(atLeast = 1, filter = GameObjectFilter.Noncreature)
        )
        effect = Effects.DrawCards(1).then(
            Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 2, EffectTarget.Self)
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "182"
        artist = "Tran Nguyen"
        imageUri = "https://cards.scryfall.io/normal/front/0/d/0d369c44-78ee-4f3c-bf2b-cddba7fe26d4.jpg?1743204706"
    }
}
