package com.wingedsheep.mtg.sets.definitions.mh1.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.ninjutsu
import com.wingedsheep.sdk.model.Rarity

/**
 * Moonblade Shinobi
 * {3}{U}
 * Creature — Human Ninja
 * 3/2
 * Ninjutsu {2}{U} ({2}{U}, Return an unblocked attacker you control to hand: Put this card onto the battlefield from your hand tapped and attacking.)
 * Whenever this creature deals combat damage to a player, create a 1/1 blue Illusion creature token with flying.
 */
val MoonbladeShinobi = card("Moonblade Shinobi") {
    manaCost = "{3}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Human Ninja"
    power = 3
    toughness = 2
    oracleText = "Ninjutsu {2}{U} ({2}{U}, Return an unblocked attacker you control to hand: Put this card onto the battlefield from your hand tapped and attacking.)\n" +
        "Whenever this creature deals combat damage to a player, create a 1/1 blue Illusion creature token with flying."

    ninjutsu("{2}{U}")

    triggeredAbility {
        trigger = Triggers.DealsCombatDamageToPlayer
        effect = Effects.CreateToken(
            power = 1,
            toughness = 1,
            colors = setOf(Color.BLUE),
            creatureTypes = setOf("Illusion"),
            keywords = setOf(Keyword.FLYING),
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "59"
        artist = "Taylor Ingvarsson"
        imageUri = "https://cards.scryfall.io/normal/front/f/4/f4ab7f30-ca75-4656-9738-1d965f18a22d.jpg?1783933141"
    }
}
