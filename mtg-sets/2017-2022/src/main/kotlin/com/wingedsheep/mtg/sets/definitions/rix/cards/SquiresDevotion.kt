package com.wingedsheep.mtg.sets.definitions.rix.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.ModifyStats

/**
 * Squire's Devotion
 * {2}{W}
 * Enchantment — Aura
 * Enchant creature
 * Enchanted creature gets +1/+1 and has lifelink.
 * When this Aura enters, create a 1/1 white Vampire creature token with lifelink.
 *
 * Both statics leave `filter` at its default [com.wingedsheep.sdk.scripting.filters.unified
 * .GroupFilter.attachedCreature] — on an Aura that is the enchanted creature.
 */
val SquiresDevotion = card("Squire's Devotion") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Enchantment — Aura"
    oracleText = "Enchant creature\n" +
        "Enchanted creature gets +1/+1 and has lifelink.\n" +
        "When this Aura enters, create a 1/1 white Vampire creature token with lifelink."

    auraTarget = Targets.Creature

    staticAbility {
        ability = ModifyStats(1, 1)
    }

    staticAbility {
        ability = GrantKeyword(Keyword.LIFELINK)
    }

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.CreateToken(
            power = 1,
            toughness = 1,
            colors = setOf(Color.WHITE),
            creatureTypes = setOf("Vampire"),
            keywords = setOf(Keyword.LIFELINK),
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "25"
        artist = "Winona Nelson"
        imageUri = "https://cards.scryfall.io/normal/front/9/1/91de07da-564d-42f3-987d-a2321c3216bc.jpg?1783935330"
        ruling(
            "2018-01-19",
            "You need a creature for Squire's Devotion to target as you cast it. There's no way " +
                "to have it enter the battlefield attached to the Vampire token it'll create."
        )
        ruling(
            "2018-01-19",
            "If the creature this Aura would enchant is an illegal target by the time Squire's " +
                "Devotion tries to resolve, the Aura spell doesn't resolve. It won't enter the " +
                "battlefield, so its ability won't trigger."
        )
    }
}
