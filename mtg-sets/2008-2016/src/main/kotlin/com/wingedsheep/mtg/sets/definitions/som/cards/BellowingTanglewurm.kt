package com.wingedsheep.mtg.sets.definitions.som.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Bellowing Tanglewurm
 * {3}{G}{G}
 * Creature — Wurm
 * 4/4
 *
 * Intimidate (This creature can't be blocked except by artifact creatures and/or creatures that share a color with it.)
 * Other green creatures you control have intimidate.
 *
 * The lord half is a plain [GrantKeyword] over green creatures you control with `excludeSelf` set:
 * the Tanglewurm has intimidate printed on it, so "other" only has to keep the grant from doubling
 * up on the source. Green is read off *projected* colour, so a creature turned green by a colour
 * changer picks the keyword up and one turned off it loses it.
 */
val BellowingTanglewurm = card("Bellowing Tanglewurm") {
    manaCost = "{3}{G}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Wurm"
    power = 4
    toughness = 4
    oracleText = "Intimidate (This creature can't be blocked except by artifact creatures and/or creatures that share a color with it.)\n" +
        "Other green creatures you control have intimidate."

    keywords(Keyword.INTIMIDATE)

    staticAbility {
        ability = GrantKeyword(
            Keyword.INTIMIDATE,
            GroupFilter(
                GameObjectFilter.Creature.withColor(Color.GREEN).youControl(),
                excludeSelf = true
            )
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "111"
        artist = "jD"
        imageUri = "https://cards.scryfall.io/normal/front/4/4/44eb3e3a-60ee-4293-a321-daa452d4c70d.jpg?1783941720"
    }
}
