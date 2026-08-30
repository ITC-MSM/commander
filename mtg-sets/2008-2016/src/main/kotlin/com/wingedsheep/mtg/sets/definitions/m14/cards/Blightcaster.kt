package com.wingedsheep.mtg.sets.definitions.m14.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Blightcaster
 * {3}{B}
 * Creature — Human Wizard
 * 2 / 3
 * Whenever you cast an enchantment spell, you may have target creature get -2/-2 until end of turn.
 */
val Blightcaster = card("Blightcaster") {
    manaCost = "{3}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Human Wizard"
    power = 2
    toughness = 3
    oracleText = "Whenever you cast an enchantment spell, you may have target creature get -2/-2 until end of turn."

    triggeredAbility {
        trigger = Triggers.YouCastEnchantment
        val victim = target("target", TargetCreature(filter = TargetFilter.Creature))
        optional = true
        effect = Effects.ModifyStats(-2, -2, victim)
        description =
            "Whenever you cast an enchantment spell, you may have target creature get -2/-2 until end of turn."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "86"
        artist = "Winona Nelson"
        flavorText = "\"Your flesh is unprepared for my gifts.\""
        imageUri = "https://cards.scryfall.io/normal/front/6/1/61752b13-255a-44d0-9fb0-5ed5680b954e.jpg"
    }
}
