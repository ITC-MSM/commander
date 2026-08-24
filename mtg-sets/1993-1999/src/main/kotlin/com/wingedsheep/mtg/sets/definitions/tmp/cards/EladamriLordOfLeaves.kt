package com.wingedsheep.mtg.sets.definitions.tmp.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Eladamri, Lord of Leaves
 * {G}{G}
 * Legendary Creature — Elf Warrior
 * 2/2
 * Other Elf creatures have forestwalk. (They can't be blocked as long as defending player controls a Forest.)
 * Other Elves have shroud. (They can't be the targets of spells or abilities.)
 */
val EladamriLordOfLeaves = card("Eladamri, Lord of Leaves") {
    manaCost = "{G}{G}"
    colorIdentity = "G"
    typeLine = "Legendary Creature — Elf Warrior"
    power = 2
    toughness = 2
    oracleText = "Other Elf creatures have forestwalk. (They can't be blocked as long as defending player controls a Forest.)\n" +
        "Other Elves have shroud. (They can't be the targets of spells or abilities.)"

    staticAbility {
        ability = GrantKeyword(
            Keyword.FORESTWALK,
            GroupFilter(GameObjectFilter.Creature.withSubtype("Elf"), excludeSelf = true)
        )
    }

    staticAbility {
        ability = GrantKeyword(
            Keyword.SHROUD,
            GroupFilter(GameObjectFilter.Permanent.withSubtype("Elf"), excludeSelf = true)
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "224"
        artist = "Ron Chironna"
        flavorText = "\"We have been patient. We have planned our attack. We are ready . . . *now*.\"\n" +
            "—Eladamri, Lord of Leaves"
        imageUri = "https://cards.scryfall.io/normal/front/0/b/0b1689f3-9dfa-4525-90b3-7af15f7eb720.jpg"
    }
}
