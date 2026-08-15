package com.wingedsheep.mtg.sets.definitions.tmt.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.MayEffect
import com.wingedsheep.sdk.scripting.predicates.CardPredicate
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * West Wind Avatar
 * {5}{G}{G}
 * Creature — Cat Spirit Avatar
 * 7/7
 *
 * Trample
 * Whenever this creature enters or attacks, you may sacrifice a token or a land.
 * If you do, you gain 3 life.
 * Disappear — At the beginning of your end step, if a permanent left the
 * battlefield under your control this turn, draw a card.
 */
val WestWindAvatar = card("West Wind Avatar") {
    manaCost = "{5}{G}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Cat Spirit Avatar"
    oracleText = "Trample\nWhenever this creature enters or attacks, you may sacrifice a token or a land. If you do, you gain 3 life.\nDisappear — At the beginning of your end step, if a permanent left the battlefield under your control this turn, draw a card."
    power = 7
    toughness = 7

    keywords(Keyword.TRAMPLE)

    // "you may sacrifice a token or a land. If you do, you gain 3 life."
    val tokenOrLand = GameObjectFilter(
        cardPredicates = listOf(
            CardPredicate.Or(listOf(CardPredicate.IsToken, CardPredicate.IsLand))
        )
    )
    val sacrificeForLife = MayEffect(
        Effects.Sacrifice(tokenOrLand, count = 1, target = EffectTarget.Controller)
            .then(Effects.GainLife(3))
    )

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = sacrificeForLife
        description = "Whenever this creature enters, you may sacrifice a token or a land. If you do, you gain 3 life."
    }

    triggeredAbility {
        trigger = Triggers.Attacks
        effect = sacrificeForLife
        description = "Whenever this creature attacks, you may sacrifice a token or a land. If you do, you gain 3 life."
    }

    triggeredAbility {
        trigger = Triggers.YourEndStep
        triggerCondition = Conditions.YouHadPermanentLeaveBattlefieldThisTurn
        effect = Effects.DrawCards(1)
        description = "Disappear — At the beginning of your end step, if a permanent left the battlefield under your control this turn, draw a card."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "137"
        artist = "Oriana Menendez"
        imageUri = "https://cards.scryfall.io/normal/front/f/4/f4b8f7e6-9bff-430b-b2f5-4308d57d1194.jpg?1771586987"
    }
}
