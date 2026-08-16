package com.wingedsheep.mtg.sets.definitions.conflux.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Sigil of the Empty Throne
 * {3}{W}{W}
 * Enchantment
 * Whenever you cast an enchantment spell, create a 4/4 white Angel creature token with flying.
 */
val SigilOfTheEmptyThrone = card("Sigil of the Empty Throne") {
    manaCost = "{3}{W}{W}"
    colorIdentity = "W"
    typeLine = "Enchantment"
    oracleText = "Whenever you cast an enchantment spell, create a 4/4 white Angel creature token with flying."

    triggeredAbility {
        trigger = Triggers.YouCastEnchantment
        effect = Effects.CreateToken(
            power = 4,
            toughness = 4,
            colors = setOf(Color.WHITE),
            creatureTypes = setOf("Angel"),
            keywords = setOf(Keyword.FLYING),
        )
        description = "Whenever you cast an enchantment spell, create a 4/4 white Angel creature token with flying."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "18"
        artist = "Cyril Van Der Haegen"
        flavorText = "When Asha left Bant, she ensured that the world would have protection and order in her absence."
        imageUri = "https://cards.scryfall.io/normal/front/9/c/9c451f63-2827-49fe-9d1d-87c87f7f5f8d.jpg?1783942490"
        ruling(
            "2021-03-19",
            "An ability that triggers when a player casts a spell resolves before the spell that caused it to trigger, but after targets have been chosen for that spell. It resolves even if that spell is countered.",
        )
        ruling(
            "2021-03-19",
            "Because it's not on the battlefield yet, casting Sigil of the Empty Throne won't cause its own ability to trigger.",
        )
    }
}
