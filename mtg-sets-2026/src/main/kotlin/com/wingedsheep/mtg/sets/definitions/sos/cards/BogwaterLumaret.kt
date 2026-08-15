package com.wingedsheep.mtg.sets.definitions.sos.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding

/**
 * Bogwater Lumaret — Secrets of Strixhaven #177
 * {B}{G} · Creature — Spirit Frog · 2/2
 *
 * Whenever this creature or another creature you control enters, you gain 1 life.
 *
 * "This creature or another creature you control enters" is a single `entersBattlefield` trigger
 * filtered to creatures you control with [TriggerBinding.ANY] — `ANY` matches both the source
 * itself and any other creature you control, so it fires once for Bogwater's own ETB and once for
 * each other creature you control that enters. You gain 1 life each time.
 */
val BogwaterLumaret = card("Bogwater Lumaret") {
    manaCost = "{B}{G}"
    colorIdentity = "BG"
    typeLine = "Creature — Spirit Frog"
    power = 2
    toughness = 2
    oracleText = "Whenever this creature or another creature you control enters, you gain 1 life."

    triggeredAbility {
        trigger = Triggers.entersBattlefield(
            filter = GameObjectFilter.Creature.youControl(),
            binding = TriggerBinding.ANY,
        )
        effect = Effects.GainLife(1)
        description = "Whenever this creature or another creature you control enters, you gain 1 life."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "177"
        artist = "Lie Setiawan"
        flavorText = "\"You will hear rumors that licking lumarets grants prophetic visions. That " +
            "is untrue. Please refrain from licking the lumarets.\"\n—Witherbloom student handbook"
        imageUri = "https://cards.scryfall.io/normal/front/7/a/7a42f51a-3377-47bb-b6fb-c0515bf1dcfb.jpg?1775938216"
    }
}
