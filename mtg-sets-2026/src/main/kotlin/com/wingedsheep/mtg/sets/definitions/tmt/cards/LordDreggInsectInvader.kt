package com.wingedsheep.mtg.sets.definitions.tmt.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.CreateTokenEffect

/**
 * Lord Dregg, Insect Invader
 * {3}{B}
 * Legendary Creature — Insect Warrior
 * 3/2
 *
 * Flying
 * Disappear — At the beginning of your end step, if a permanent left the
 * battlefield under your control this turn, create a 1/1 black Insect Warrior
 * creature token with flying.
 * {3}{G}, Sacrifice a token: Draw a card.
 */
val LordDreggInsectInvader = card("Lord Dregg, Insect Invader") {
    manaCost = "{3}{B}"
    colorIdentity = "BG"
    typeLine = "Legendary Creature — Insect Warrior"
    oracleText = "Flying\nDisappear — At the beginning of your end step, if a permanent left the battlefield under your control this turn, create a 1/1 black Insect Warrior creature token with flying.\n{3}{G}, Sacrifice a token: Draw a card."
    power = 3
    toughness = 2

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.YourEndStep
        triggerCondition = Conditions.YouHadPermanentLeaveBattlefieldThisTurn
        effect = CreateTokenEffect(
            power = 1,
            toughness = 1,
            colors = setOf(Color.BLACK),
            creatureTypes = setOf("Insect", "Warrior"),
            keywords = setOf(Keyword.FLYING)
        )
        description = "Disappear — At the beginning of your end step, if a permanent left the battlefield under your control this turn, create a 1/1 black Insect Warrior creature token with flying."
    }

    activatedAbility {
        cost = Costs.Composite(
            Costs.Mana("{3}{G}"),
            Costs.Sacrifice(GameObjectFilter.Token)
        )
        effect = Effects.DrawCards(1)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "65"
        artist = "Brian Valeza"
        imageUri = "https://cards.scryfall.io/normal/front/8/d/8d1a9c8a-d0b6-4d83-a168-8078de4b14c7.jpg?1771586872"
    }
}
