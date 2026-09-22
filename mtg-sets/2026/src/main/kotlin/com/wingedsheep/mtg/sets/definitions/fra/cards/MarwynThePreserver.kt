package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetObject

val MarwynThePreserver = card("Marwyn, the Preserver") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Legendary Creature — Elf Druid"
    oracleText = "Lands you control have hexproof. (They can't be the targets of spells or abilities your opponents control.)\n{2}: Return target land card from your graveyard to your hand."
    power = 3
    toughness = 2

    staticAbility {
        ability = GrantKeyword(Keyword.HEXPROOF, GroupFilter(GameObjectFilter.Land.youControl()))
    }

    activatedAbility {
        cost = Costs.Mana("{2}")
        val land = target("land", TargetObject(
            filter = TargetFilter(GameObjectFilter.Land.ownedByYou(), zone = Zone.GRAVEYARD)
        ))
        effect = Effects.ReturnToHand(land)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "263"
        artist = "Quintin Gleim"
        flavorText = "\"We are Llanowar's children, but also its protectors. Our destinies are one.\""
        imageUri = "https://cards.scryfall.io/normal/front/9/0/90f33f99-7bc5-42e1-815e-bfb4c2b74107.jpg?1789644564"
        inBooster = false
    }
}
