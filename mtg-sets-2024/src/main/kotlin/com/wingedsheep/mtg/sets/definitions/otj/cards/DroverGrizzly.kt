package com.wingedsheep.mtg.sets.definitions.otj.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Drover Grizzly
 * {2}{G}
 * Creature — Bear Mount
 * 4/2
 *
 * Whenever this creature attacks while saddled, creatures you control gain trample until end of turn.
 * Saddle 1 (Tap any number of other creatures you control with total power 1 or more: This Mount
 * becomes saddled until end of turn. Saddle only as a sorcery.)
 *
 * "While saddled" is an intervening-if (CR 603.4) on the attack trigger: it checks the source's
 * SaddledComponent both when the trigger would fire and again as it resolves.
 */
val DroverGrizzly = card("Drover Grizzly") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Bear Mount"
    power = 4
    toughness = 2
    oracleText = "Whenever this creature attacks while saddled, creatures you control gain trample " +
        "until end of turn.\n" +
        "Saddle 1 (Tap any number of other creatures you control with total power 1 or more: This " +
        "Mount becomes saddled until end of turn. Saddle only as a sorcery.)"

    keywordAbility(KeywordAbility.saddle(1))

    triggeredAbility {
        trigger = Triggers.Attacks
        triggerCondition = Conditions.SourceIsSaddled
        effect = Patterns.Group.grantKeywordToAll(Keyword.TRAMPLE, Filters.Group.creaturesYouControl)
        description = "creatures you control gain trample until end of turn."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "161"
        artist = "Adrián Rodríguez Pérez"
        imageUri = "https://cards.scryfall.io/normal/front/5/6/560062cd-34f8-4d30-9e25-099b03961724.jpg?1712355911"
    }
}
