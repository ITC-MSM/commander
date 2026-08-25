package com.wingedsheep.mtg.sets.definitions.mh1.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Webweaver Changeling
 * {3}{G}{G}
 * Creature — Shapeshifter
 * 3/5
 * Changeling (This card is every creature type.)
 * Reach
 * When this creature enters, if there are three or more creature cards in your graveyard, you gain 5 life.
 */
val WebweaverChangeling = card("Webweaver Changeling") {
    manaCost = "{3}{G}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Shapeshifter"
    power = 3
    toughness = 5
    oracleText = "Changeling (This card is every creature type.)\n" +
        "Reach\n" +
        "When this creature enters, if there are three or more creature cards in your graveyard, you gain 5 life."

    keywords(Keyword.CHANGELING, Keyword.REACH)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        interveningIf = Conditions.CreatureCardsInGraveyardAtLeast(3)
        effect = Effects.GainLife(5)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "192"
        artist = "Nicholas Gregory"
        flavorText = "Eight legs carrying endless phobias."
        imageUri = "https://cards.scryfall.io/normal/front/9/1/91571765-8da8-49f9-a776-7778d86cfb99.jpg?1783933086"
    }
}
