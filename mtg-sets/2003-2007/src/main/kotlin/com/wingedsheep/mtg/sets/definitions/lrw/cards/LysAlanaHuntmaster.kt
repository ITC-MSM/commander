package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Lys Alana Huntmaster
 * {2}{G}{G}
 * Creature — Elf Warrior
 * 3/3
 * Whenever you cast an Elf spell, you may create a 1/1 green Elf Warrior creature token.
 *
 * The trigger reads the *spell's* subtype, so Lorwyn's Kindred Elf cards feed it alongside Elf
 * creature spells. Ruling (2016-06-08): the ability resolves before the Elf spell that triggered
 * it, which falls out of the trigger going on the stack above the spell.
 */
val LysAlanaHuntmaster = card("Lys Alana Huntmaster") {
    manaCost = "{2}{G}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Elf Warrior"
    power = 3
    toughness = 3
    oracleText = "Whenever you cast an Elf spell, you may create a 1/1 green Elf Warrior creature token."

    triggeredAbility {
        trigger = Triggers.YouCastSubtype(Subtype.ELF)
        optional = true
        effect = Effects.CreateToken(
            power = 1,
            toughness = 1,
            colors = setOf(Color.GREEN),
            creatureTypes = setOf("Elf", "Warrior"),
            imageUri = "https://cards.scryfall.io/normal/front/2/7/27b171ac-b2ef-4a80-92d1-6d9e71f3e3ca.jpg?1783942838",
        )
        description = "you may create a 1/1 green Elf Warrior creature token."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "229"
        artist = "Pete Venters"
        flavorText = "From the highest tiers of Dawn's Light Palace to the deepest shade of Wren's Run, the silver notes of the horn shimmer through the air, and all who hear it feel its pull."
        imageUri = "https://cards.scryfall.io/normal/front/8/d/8d21f992-f982-415e-9d77-d11d8c931741.jpg?1783942860"
        ruling("2016-06-08", "Lys Alana Huntmaster's ability will resolve before the Elf spell that caused it to trigger.")
    }
}
