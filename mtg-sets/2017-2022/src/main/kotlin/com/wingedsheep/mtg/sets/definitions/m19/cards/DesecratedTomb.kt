package com.wingedsheep.mtg.sets.definitions.m19.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Desecrated Tomb
 * {3}
 * Artifact
 * Whenever one or more creature cards leave your graveyard, create a 1/1 black Bat creature token with flying.
 *
 * "One or more … leave" is CR 603.2c batch wording, so the batching
 * [Triggers.CardsLeaveYourGraveyard] is the right shape: a mass reanimation or a graveyard-exiling
 * sweep fires this exactly once, no matter how many creature cards moved or where they went.
 */
val DesecratedTomb = card("Desecrated Tomb") {
    manaCost = "{3}"
    colorIdentity = ""
    typeLine = "Artifact"
    oracleText = "Whenever one or more creature cards leave your graveyard, create a 1/1 black Bat creature token with flying."

    triggeredAbility {
        trigger = Triggers.CardsLeaveYourGraveyard(GameObjectFilter.Creature)
        effect = Effects.CreateToken(
            power = 1,
            toughness = 1,
            colors = setOf(Color.BLACK),
            creatureTypes = setOf("Bat"),
            keywords = setOf(Keyword.FLYING)
        )
        description = "Whenever one or more creature cards leave your graveyard, create a 1/1 " +
            "black Bat creature token with flying."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "230"
        artist = "Dimitar Marinski"
        flavorText = "The grave robbers were startled, for the door to the mausoleum was already open."
        imageUri = "https://cards.scryfall.io/normal/front/4/5/458ce930-c100-4ef5-b75a-a18051282f8c.jpg"
    }
}
