package com.wingedsheep.mtg.sets.definitions.war.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Tithebearer Giant
 * {5}{B}
 * Creature — Giant Warrior
 * 4/5
 * When this creature enters, you draw a card and you lose 1 life.
 */
val TithebearerGiant = card("Tithebearer Giant") {
    manaCost = "{5}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Giant Warrior"
    oracleText = "When this creature enters, you draw a card and you lose 1 life."
    power = 4
    toughness = 5

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        // `Effects.LoseLife` defaults its target to TargetOpponent, so "you lose 1 life"
        // must name the controller explicitly.
        effect = Effects.DrawCards(1) then Effects.LoseLife(1, EffectTarget.Controller)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "107"
        artist = "Wisnu Tan"
        flavorText = "\"I've always said full war chests win wars, but I didn't mean by bashing enemy skulls with them.\"\n—Teysa"
        imageUri = "https://cards.scryfall.io/normal/front/3/c/3c305ccb-62f5-496a-a205-e818e34ead82.jpg"
    }
}
