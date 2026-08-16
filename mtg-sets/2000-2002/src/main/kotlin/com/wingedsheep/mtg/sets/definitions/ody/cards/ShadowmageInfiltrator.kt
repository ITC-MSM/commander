package com.wingedsheep.mtg.sets.definitions.ody.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.MayEffect

/**
 * Shadowmage Infiltrator
 * {1}{U}{B}
 * Creature — Human Wizard
 * 1/3
 *
 * Fear
 * Whenever this creature deals combat damage to a player, you may draw a card.
 */
val ShadowmageInfiltrator = card("Shadowmage Infiltrator") {
    manaCost = "{1}{U}{B}"
    colorIdentity = "UB"
    typeLine = "Creature — Human Wizard"
    power = 1
    toughness = 3
    oracleText = "Fear (This creature can't be blocked except by artifact creatures and/or black creatures.)\n" +
        "Whenever this creature deals combat damage to a player, you may draw a card."

    keywords(Keyword.FEAR)

    triggeredAbility {
        trigger = Triggers.DealsCombatDamageToPlayer
        effect = MayEffect(Effects.DrawCards(1))
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "294"
        artist = "Rick Farrell"
        imageUri = "https://cards.scryfall.io/normal/front/9/3/932ce702-565f-4d8c-b9fc-2d7c939ef7d7.jpg"
    }
}
