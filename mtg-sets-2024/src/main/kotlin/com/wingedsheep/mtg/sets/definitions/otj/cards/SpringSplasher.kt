package com.wingedsheep.mtg.sets.definitions.otj.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Spring Splasher
 * {1}{U}
 * Creature — Frog Beast
 * 2/1
 *
 * Whenever this creature attacks, target creature defending player controls
 * gets -3/-0 until end of turn.
 */
val SpringSplasher = card("Spring Splasher") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Frog Beast"
    power = 2
    toughness = 1
    oracleText = "Whenever this creature attacks, target creature defending player controls gets -3/-0 until end of turn."

    triggeredAbility {
        trigger = Triggers.Attacks
        val creature = target(
            "creature defending player controls",
            Targets.CreatureOpponentControls
        )
        effect = Effects.ModifyStats(-3, 0, creature)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "69"
        artist = "Loïc Canavaggia"
        flavorText = "It once escaped a cook's pot. Now it fears neither heat nor human."
        imageUri = "https://cards.scryfall.io/normal/front/b/6/b6822d12-1a25-42e7-94cc-71bd29daed93.jpg?1712355509"
    }
}
