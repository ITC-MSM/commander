package com.wingedsheep.mtg.sets.definitions.snc.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Social Climber
 * {2}{G}
 * Creature — Human Druid
 * 3 / 2
 * Alliance — Whenever another creature you control enters, you gain 1 life.
 *
 * "Alliance" is a pure ability word, so this is the plain [Triggers.OtherCreatureEnters] (OTHER
 * binding over creatures you control); the ability word lives only in the printed text.
 */
val SocialClimber = card("Social Climber") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Human Druid"
    oracleText = "Alliance — Whenever another creature you control enters, you gain 1 life."
    power = 3
    toughness = 2

    triggeredAbility {
        trigger = Triggers.OtherCreatureEnters
        effect = Effects.GainLife(1)
        description = "Alliance — Whenever another creature you control enters, you gain 1 life."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "157"
        artist = "Carly Mazur"
        flavorText = "\"It's all about who you know, darling. And now you're lucky enough to know me.\""
        imageUri = "https://cards.scryfall.io/normal/front/a/9/a9fb74fd-767f-4dd4-822a-828d59f633ad.jpg?1783923098"
    }
}
