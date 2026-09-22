package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetObject

val StingcasterMage = card("Stingcaster Mage") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Human Wizard"
    power = 2
    toughness = 1
    oracleText = "Haste\nWhen this creature enters, target instant or sorcery card in your graveyard gains flashback until end of turn. The flashback cost is equal to its mana cost."

    keywords(Keyword.HASTE)
    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val card = target("instant or sorcery card in your graveyard",
            TargetObject(filter = TargetFilter.InstantOrSorceryInGraveyard.ownedByYou()))
        effect = Effects.GrantFlashback(card)
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "93"
        artist = "Matt Stewart"
        imageUri = "https://cards.scryfall.io/normal/front/2/d/2d8e8e5f-3bf5-490d-aa9c-9df2b26f1460.jpg?1788329303"
        inBooster = false
    }
}
