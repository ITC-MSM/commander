package com.wingedsheep.mtg.sets.definitions.xln.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Pterodon Knight
 * {3}{W}
 * Creature — Human Knight
 * 3/3
 *
 * This creature has flying as long as you control a Dinosaur.
 */
val PterodonKnight = card("Pterodon Knight") {
    manaCost = "{3}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Knight"
    oracleText = "This creature has flying as long as you control a Dinosaur."
    power = 3
    toughness = 3

    staticAbility {
        ability = GrantKeyword(Keyword.FLYING, GroupFilter.source())
        condition = Conditions.ControlPermanentOfType(Subtype.DINOSAUR)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "28"
        artist = "Anthony Palumbo"
        flavorText = "\"To rise like the sun—there is no greater feeling.\""
        imageUri = "https://cards.scryfall.io/normal/front/4/1/41f00a04-1336-4830-a6c2-514848afefd2.jpg"
    }
}
