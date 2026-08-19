package com.wingedsheep.mtg.sets.definitions.m13.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Bloodhunter Bat
 * {3}{B}
 * Creature — Bat
 * 2/2
 *
 * Flying
 * When this creature enters, target player loses 2 life and you gain 2 life.
 */
val BloodhunterBat = card("Bloodhunter Bat") {
    manaCost = "{3}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Bat"
    oracleText = "Flying\nWhen this creature enters, target player loses 2 life and you gain 2 life."
    power = 2
    toughness = 2

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val player = target("target player", Targets.Player)
        effect = Effects.DrainLife(2, from = player, to = EffectTarget.Controller)
        description = "When this creature enters, target player loses 2 life and you gain 2 life."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "82"
        artist = "Tomasz Jedruszek"
        flavorText = "It returns eager to share the feast of blood and gore with its ghoulish master."
        imageUri = "https://cards.scryfall.io/normal/front/9/9/99c10705-6e0e-46f6-a64c-0095b2796aaf.jpg?1783940499"
    }
}
