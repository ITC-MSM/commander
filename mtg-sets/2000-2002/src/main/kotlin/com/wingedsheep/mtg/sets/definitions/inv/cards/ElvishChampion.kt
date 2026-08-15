package com.wingedsheep.mtg.sets.definitions.inv.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Elvish Champion
 * {1}{G}{G}
 * Creature — Elf
 * 2/2
 * Other Elf creatures get +1/+1 and have forestwalk.
 *
 * Affects Elves controlled by all players, not just yours (the filter is unscoped by
 * controller). Reworded by Oracle so it no longer gives itself the bonus — handled here
 * via excludeSelf on both static abilities.
 */
val ElvishChampion = card("Elvish Champion") {
    manaCost = "{1}{G}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Elf"
    power = 2
    toughness = 2
    oracleText = "Other Elf creatures get +1/+1 and have forestwalk. " +
        "(They can't be blocked as long as defending player controls a Forest.)"

    staticAbility {
        ability = ModifyStats(
            powerBonus = 1,
            toughnessBonus = 1,
            filter = GroupFilter(GameObjectFilter.Creature.withSubtype("Elf"), excludeSelf = true)
        )
    }

    staticAbility {
        ability = GrantKeyword(
            Keyword.FORESTWALK,
            GroupFilter(GameObjectFilter.Creature.withSubtype("Elf"), excludeSelf = true)
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "186"
        artist = "D. Alexander Gregory"
        flavorText = "\"For what are leaves but countless blades\n" +
            "To fight a countless foe on high.\"\n" +
            "—Skyshroud hymn"
        imageUri = "https://cards.scryfall.io/normal/front/c/1/c19bb473-03b0-4e6d-a7da-0ec1e7707a68.jpg?1687904220"
        ruling("2004-10-04", "It affects Elves controlled by all players, not just yours.")
        ruling("2005-08-01", "This card is now an Elf but has been reworded so that it does not give itself the bonus.")
    }
}
