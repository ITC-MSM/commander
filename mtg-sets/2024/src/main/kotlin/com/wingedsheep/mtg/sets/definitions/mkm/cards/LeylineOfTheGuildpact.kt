package com.wingedsheep.mtg.sets.definitions.mkm.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.mayBeginGameOnBattlefield
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantColor
import com.wingedsheep.sdk.scripting.GrantSubtype
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Leyline of the Guildpact — Murders at Karlov Manor #217
 * {G/W}{G/U}{B/G}{R/G} · Enchantment
 *
 * The five [GrantColor] abilities share the nonland-permanents-you-control filter and combine in
 * layer 5 to make every affected permanent all colors. The five [GrantSubtype] abilities similarly
 * add every basic land type in layer 4 without replacing a land's printed types or abilities; the
 * engine derives the corresponding intrinsic mana abilities from those projected basic land types.
 */
val LeylineOfTheGuildpact = card("Leyline of the Guildpact") {
    manaCost = "{G/W}{G/U}{B/G}{R/G}"
    colorIdentity = "WUBRG"
    typeLine = "Enchantment"
    oracleText = "If this card is in your opening hand, you may begin the game with it on the battlefield.\n" +
        "Each nonland permanent you control is all colors.\n" +
        "Lands you control are every basic land type in addition to their other types."

    mayBeginGameOnBattlefield()

    val nonlandPermanentsYouControl = GroupFilter(GameObjectFilter.NonlandPermanent.youControl())
    Color.entries.forEach { color ->
        staticAbility {
            ability = GrantColor(color, nonlandPermanentsYouControl)
        }
    }

    val landsYouControl = GroupFilter(GameObjectFilter.Land.youControl())
    listOf("Plains", "Island", "Swamp", "Mountain", "Forest").forEach { subtype ->
        staticAbility {
            ability = GrantSubtype(subtype, landsYouControl)
        }
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "217"
        artist = "Daarken"
        imageUri = "https://cards.scryfall.io/normal/front/b/f/bf6e59be-f959-4f4a-8c2d-b7c441e88135.jpg?1783912844"
        ruling("2024-02-02", "Each land you control will have the land types Plains, Island, Swamp, Mountain, and Forest. They'll also have the mana ability of each basic land type. They'll still have their other subtypes and abilities.")
        ruling("2024-02-02", "Giving a land additional basic land types doesn't change its name or whether it's legendary or basic.")
    }
}
