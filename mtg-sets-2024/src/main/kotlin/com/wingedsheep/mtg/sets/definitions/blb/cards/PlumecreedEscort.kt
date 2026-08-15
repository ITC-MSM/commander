package com.wingedsheep.mtg.sets.definitions.blb.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Plumecreed Escort
 * {1}{U}
 * Creature — Bird Scout
 * 2/1
 *
 * Flash
 * Flying
 * When this creature enters, target creature you control gains hexproof until end of turn.
 */
val PlumecreedEscort = card("Plumecreed Escort") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Bird Scout"
    oracleText = "Flash\nFlying\nWhen this creature enters, target creature you control gains hexproof until end of turn."
    power = 2
    toughness = 1

    keywords(Keyword.FLASH, Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val creature = target("creature you control", Targets.CreatureYouControl)
        effect = Effects.GrantHexproof(creature)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "65"
        artist = "Manuel Castañón"
        flavorText = "\"I defend Valley and its beauty. What better weapon to wield than beauty itself?\""
        imageUri = "https://cards.scryfall.io/normal/front/f/7/f71320ed-2f30-49ce-bcb0-19aebba3f0e8.jpg?1721426204"
    }
}
