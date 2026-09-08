package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.champion
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Wren's Run Packmaster
 * {3}{G}
 * Creature — Elf Warrior
 * 5/5
 *
 * Champion an Elf
 * {2}{G}: Create a 2/2 green Wolf creature token.
 * Wolves you control have deathtouch.
 *
 * The lord clause is every Wolf permanent you control, not only the tokens this makes
 * (2007-10-01 ruling), and the Packmaster is not itself a Wolf so there is no self-exclusion.
 */
val WrensRunPackmaster = card("Wren's Run Packmaster") {
    manaCost = "{3}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Elf Warrior"
    power = 5
    toughness = 5
    oracleText = "Champion an Elf (When this creature enters, sacrifice it unless you exile " +
        "another Elf you control. When this creature leaves the battlefield, that card returns " +
        "to the battlefield.)\n" +
        "{2}{G}: Create a 2/2 green Wolf creature token.\n" +
        "Wolves you control have deathtouch."

    champion(Subtype.ELF)

    activatedAbility {
        cost = Costs.Mana("{2}{G}")
        effect = Effects.CreateToken(
            power = 2,
            toughness = 2,
            colors = setOf(Color.GREEN),
            creatureTypes = setOf("Wolf"),
            imageUri = "https://cards.scryfall.io/normal/front/0/2/02a80dc2-0811-4df0-95f1-dd80ce9e24de.jpg?1783942837"
        )
        description = "Create a 2/2 green Wolf creature token."
    }

    staticAbility {
        ability = GrantKeyword(
            keyword = Keyword.DEATHTOUCH,
            filter = GroupFilter(
                GameObjectFilter.Permanent.withSubtype(Subtype.WOLF).youControl()
            )
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "244"
        artist = "Mark Zug"
        imageUri = "https://cards.scryfall.io/normal/front/3/e/3e97f29a-1551-4e75-80e8-2dd31cb6c0db.jpg?1783942855"

        ruling(
            "2007-10-01",
            "Wren's Run Packmaster gives deathtouch to all Wolf permanents you control, not just " +
                "the tokens it creates."
        )
        ruling(
            "2009-10-01",
            "A Wolf's deathtouch ability will apply if both Wren's Run Packmaster and that Wolf " +
                "are on the battlefield at the time the Wolf deals damage. The creature dealt " +
                "damage by the Wolf will be destroyed the next time state-based actions are " +
                "checked, even if the Wolf or Wren's Run Packmaster leaves the battlefield."
        )
    }
}
