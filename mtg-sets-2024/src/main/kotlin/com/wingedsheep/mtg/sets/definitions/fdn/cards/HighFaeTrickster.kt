package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantFlashToSpellType

/**
 * High Fae Trickster
 * {3}{U}
 * Creature — Faerie Wizard
 * 4/2
 * Flash (You may cast this spell any time you could cast an instant.)
 * Flying
 * You may cast spells as though they had flash.
 *
 * The third line is a [GrantFlashToSpellType] static over every spell the controller casts
 * (`GameObjectFilter.Any`, `controllerOnly = true`) — the unrestricted, you-only sibling of
 * Valley Floodcaller's noncreature-only grant.
 */
val HighFaeTrickster = card("High Fae Trickster") {
    manaCost = "{3}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Faerie Wizard"
    power = 4
    toughness = 2
    oracleText = "Flash (You may cast this spell any time you could cast an instant.)\n" +
        "Flying\n" +
        "You may cast spells as though they had flash."

    keywords(Keyword.FLASH, Keyword.FLYING)

    staticAbility {
        ability = GrantFlashToSpellType(
            filter = GameObjectFilter.Any,
            controllerOnly = true
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "40"
        artist = "Justyna Dura"
        flavorText = "To the High Fae, spells that take a human years to master come as easily as breathing."
        imageUri = "https://cards.scryfall.io/normal/front/7/f/7f1b93ea-1ec1-4010-9343-765742f5088b.jpg?1782689229"
    }
}
