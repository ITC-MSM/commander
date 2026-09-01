package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Tolsimir Wolfblood
 * {4}{G}{W}
 * Legendary Creature — Elf Warrior
 * 3/4
 *
 * Other green creatures you control get +1/+1.
 * Other white creatures you control get +1/+1.
 * {T}: Create Voja, a legendary 2/2 green and white Wolf creature token.
 *
 * Two separate lord lines rather than one "green or white" filter: a creature that is *both*
 * green and white — Voja itself, or another Selesnya gold creature — matches both and so gets
 * +2/+2, which is exactly the printed ruling of 2005-10-01. Collapsing them into a single
 * colour-set filter would apply the bonus once and quietly halve it.
 *
 * The token is one of the few whose name is not its creature type: it is named "Voja" and its
 * type is Wolf, so [Effects.CreateToken] gets both `name` and `creatureTypes` explicitly, plus
 * `legendary = true` so a second copy meets the legend rule.
 */
val TolsimirWolfblood = card("Tolsimir Wolfblood") {
    manaCost = "{4}{G}{W}"
    colorIdentity = "WG"
    typeLine = "Legendary Creature — Elf Warrior"
    oracleText = "Other green creatures you control get +1/+1.\n" +
        "Other white creatures you control get +1/+1.\n" +
        "{T}: Create Voja, a legendary 2/2 green and white Wolf creature token."
    power = 3
    toughness = 4

    staticAbility {
        ability = ModifyStats(
            powerBonus = 1,
            toughnessBonus = 1,
            filter = GroupFilter(
                GameObjectFilter.Creature.withColor(Color.GREEN).youControl(),
                excludeSelf = true
            )
        )
    }

    staticAbility {
        ability = ModifyStats(
            powerBonus = 1,
            toughnessBonus = 1,
            filter = GroupFilter(
                GameObjectFilter.Creature.withColor(Color.WHITE).youControl(),
                excludeSelf = true
            )
        )
    }

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.CreateToken(
            power = 2,
            toughness = 2,
            colors = setOf(Color.GREEN, Color.WHITE),
            creatureTypes = setOf("Wolf"),
            name = "Voja",
            legendary = true,
            imageUri = "https://cards.scryfall.io/normal/front/a/f/af8ba142-4f39-45e4-8872-b6e348fd760c.jpg?1783913148"
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "236"
        artist = "Donato Giancola"
        imageUri = "https://cards.scryfall.io/normal/front/0/6/069ac859-e0ef-4685-bad3-5c741102b5b9.jpg?1783943608"
        ruling(
            "2005-10-01",
            "Other creatures you control that are both green and white, including Voja, get +2/+2."
        )
        ruling(
            "2005-10-01",
            "The token is named \"Voja\" and has creature type \"Wolf.\" This is different from " +
                "most creature tokens, where the name and creature type are the same."
        )
        ruling(
            "2013-07-01",
            "The \"legend rule\" means that creating a second Voja while one is already under " +
                "your control will result in one of them being put into its owner's graveyard " +
                "(where it promptly ceases to exist). You choose which of the two remains on the " +
                "battlefield and which is put into the graveyard."
        )
    }
}
