package com.wingedsheep.mtg.sets.definitions.mkm.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.effects.WardCost
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Voja, Jaws of the Conclave — Murders at Karlov Manor #432
 * {2}{R}{G}{W} · Legendary Creature — Wolf · 5/5 · Mythic
 *
 * Both tribal counts are evaluated as the attack trigger resolves. Every creature receives the
 * same number of +1/+1 counters, including Voja itself when it is still on the battlefield, and
 * the subsequent draw uses the independently evaluated number of Wolves the player controls.
 */
val VojaJawsOfTheConclave = card("Voja, Jaws of the Conclave") {
    manaCost = "{2}{R}{G}{W}"
    colorIdentity = "RGW"
    typeLine = "Legendary Creature — Wolf"
    oracleText = "Vigilance, trample, ward {3}\n" +
        "Whenever Voja attacks, put X +1/+1 counters on each creature you control, where X is " +
        "the number of Elves you control. Draw a card for each Wolf you control."
    power = 5
    toughness = 5

    keywords(Keyword.VIGILANCE, Keyword.TRAMPLE)
    keywordAbility(KeywordAbility.Ward(WardCost.Mana("{3}")))

    triggeredAbility {
        trigger = Triggers.Attacks
        effect = Effects.ForEachInGroup(
            GroupFilter(GameObjectFilter.Creature.youControl()),
            Effects.AddDynamicCounters(
                counterType = Counters.PLUS_ONE_PLUS_ONE,
                amount = DynamicAmounts
                    .battlefield(Player.You, GameObjectFilter.Creature.withSubtype(Subtype.ELF))
                    .count(),
                target = EffectTarget.Self,
            ),
        ) then Effects.DrawCards(
            DynamicAmounts
                .battlefield(Player.You, GameObjectFilter.Creature.withSubtype(Subtype.WOLF))
                .count()
        )
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "432"
        artist = "Valera Lutfullina"
        imageUri = "https://cards.scryfall.io/normal/front/b/f/bfa1bd2f-25bd-4fbd-877b-cef00ab7f92f.jpg?1783912763"
    }
}
