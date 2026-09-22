package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

val SaheeliJewelOfAvishkar = card("Saheeli, Jewel of Avishkar") {
    manaCost = "{2}{U}{R}"
    colorIdentity = "UR"
    typeLine = "Legendary Creature — Human Artificer"
    power = 2
    toughness = 4
    oracleText = "Thopters you control have haste.\n" +
        "Whenever you cast a noncreature spell, create a 1/1 colorless Thopter artifact creature token with flying."

    staticAbility {
        ability = GrantKeyword(
            Keyword.HASTE,
            GroupFilter(GameObjectFilter.Permanent.withSubtype(Subtype.THOPTER).youControl())
        )
    }

    triggeredAbility {
        trigger = Triggers.YouCastNoncreature
        effect = Effects.CreateToken(
            power = 1,
            toughness = 1,
            creatureTypes = setOf("Thopter"),
            keywords = setOf(Keyword.FLYING),
            artifactToken = true,
            imageUri = "https://cards.scryfall.io/normal/front/b/f/bfd6132f-c96b-4ce0-ac4d-c46356afc767.jpg?1789735223"
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "275"
        artist = "Billy Christian"
        flavorText = "\"Life isn't worth living without freedom of expression.\""
        imageUri = "https://cards.scryfall.io/normal/front/2/8/28d84ef6-e190-46d4-882d-1cea5e111e2a.jpg?1789644883"
        inBooster = false
    }
}
