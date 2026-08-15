package com.wingedsheep.mtg.sets.definitions.eoe.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Sunset Saboteur
 * {1}{B}
 * Creature — Human Rogue
 * Menace
 * Ward—Discard a card.
 * Whenever this creature attacks, put a +1/+1 counter on target creature an opponent controls.
 * 4/1
 */
val SunsetSaboteur = card("Sunset Saboteur") {
    manaCost = "{1}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Human Rogue"
    power = 4
    toughness = 1
    oracleText = "Menace\nWard—Discard a card.\nWhenever this creature attacks, put a +1/+1 counter on target creature an opponent controls."

    keywords(Keyword.MENACE)
    keywordAbility(KeywordAbility.wardDiscard())

    triggeredAbility {
        trigger = Triggers.Attacks
        val target = target("target creature an opponent controls", Targets.CreatureOpponentControls)
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, target)
        description = "Whenever this creature attacks, put a +1/+1 counter on target creature an opponent controls."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "116"
        artist = "Mirko Failoni"
        flavorText = "\"Lightsick fools. All suns eventually set.\""
        imageUri = "https://cards.scryfall.io/normal/front/3/9/396bca07-82ba-49b7-b79e-7784b3a06d48.jpg?1752947024"
    }
}
