package com.wingedsheep.mtg.sets.definitions.spm.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ConditionalStaticAbility
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.SetBasePowerToughnessStatic

/**
 * Doc Ock, Sinister Scientist
 * {4}{U}
 * Legendary Creature — Human Scientist Villain
 * 4/5
 * As long as there are eight or more cards in your graveyard, Doc Ock has base power and toughness 8/8.
 * As long as you control another Villain, Doc Ock has hexproof.
 */
val DocOckSinisterScientist = card("Doc Ock, Sinister Scientist") {
    manaCost = "{4}{U}"
    colorIdentity = "U"
    typeLine = "Legendary Creature — Human Scientist Villain"
    power = 4
    toughness = 5
    oracleText = "As long as there are eight or more cards in your graveyard, Doc Ock has base power and toughness 8/8.\n" +
        "As long as you control another Villain, Doc Ock has hexproof. " +
        "(He can't be the target of spells or abilities your opponents control.)"

    staticAbility {
        ability = ConditionalStaticAbility(
            ability = SetBasePowerToughnessStatic(8, 8, Filters.Self),
            condition = Conditions.CardsInGraveyardAtLeast(8),
        )
    }

    staticAbility {
        ability = ConditionalStaticAbility(
            ability = GrantKeyword(Keyword.HEXPROOF, Filters.Self),
            condition = Conditions.YouControl(
                GameObjectFilter.Any.withSubtype("Villain"),
                excludeSelf = true,
            ),
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "29"
        artist = "Piotr Dura"
        flavorText = "\"You dared to mock me before. Where are your taunts now, Spider-Man?\""
        imageUri = "https://cards.scryfall.io/normal/front/d/e/de16ad65-c8c3-48c0-9d13-5af91b4e6f01.jpg?1757376920"
    }
}
