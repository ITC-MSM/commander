package com.wingedsheep.mtg.sets.definitions.bro.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Dwarven Forge-Chanter
 * {1}{R}
 * Creature — Dwarf Wizard
 * 1/3
 * Ward—Pay 2 life. (Whenever this creature becomes the target of a spell or ability an opponent controls, counter it unless that player pays 2 life.)
 * Prowess (Whenever you cast a noncreature spell, this creature gets +1/+1 until end of turn.)
 */
val DwarvenForgeChanter = card("Dwarven Forge-Chanter") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Dwarf Wizard"
    power = 1
    toughness = 3
    oracleText = "Ward—Pay 2 life. (Whenever this creature becomes the target of a spell or ability an opponent controls, counter it unless that player pays 2 life.)\nProwess (Whenever you cast a noncreature spell, this creature gets +1/+1 until end of turn.)"

    // Ward—Pay 2 life (CR 702.21a). The bare `Keyword.WARD` marker is derived from this ability by
    // the builder, so it is not restated here.
    keywordAbility(KeywordAbility.wardLife(2))
    prowess()

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "131"
        artist = "Bartłomiej Gaweł"
        imageUri = "https://cards.scryfall.io/normal/front/b/b/bbd6a95a-11b9-43aa-b293-20a3102bae71.jpg?1783920070"
        ruling("2022-10-14", "Any spell you cast that doesn't have the type creature will cause prowess to trigger. If a spell has multiple types, and one of those types is creature (such as an artifact creature), casting it won't cause prowess to trigger. Playing a land also won't cause prowess to trigger.")
        ruling("2022-10-14", "Prowess goes on the stack on top of the spell that caused it to trigger. It will resolve before that spell.")
        ruling("2022-10-14", "Once it triggers, prowess isn't connected to the spell that caused it to trigger. If that spell is countered, prowess will still resolve.")
    }
}
