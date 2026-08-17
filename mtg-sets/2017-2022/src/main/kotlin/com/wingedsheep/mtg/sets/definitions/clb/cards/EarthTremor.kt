package com.wingedsheep.mtg.sets.definitions.clb.cards

import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Earth Tremor
 * {3}{R}
 * Instant
 * Earth Tremor deals damage to target creature or planeswalker equal to the number of lands you control.
 *
 * The whole card is one [Effects.DealDamage] whose amount is the dynamic
 * [DynamicAmounts.landsYouControl] board count rather than a fixed number, so the damage is
 * recomputed on resolution; [Targets.CreatureOrPlaneswalker] carries the target clause.
 */
val EarthTremor = card("Earth Tremor") {
    manaCost = "{3}{R}"
    colorIdentity = "R"
    typeLine = "Instant"
    oracleText = "Earth Tremor deals damage to target creature or planeswalker equal to the number of lands you control."

    spell {
        val t = target("target", Targets.CreatureOrPlaneswalker)
        effect = Effects.DealDamage(DynamicAmounts.landsYouControl(), t)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "171"
        artist = "Borja Pindado"
        flavorText = "\"Every cave-in is Moradin telling you to leave the earth where it is.\"\n—Halgar, dwarven recluse"
        imageUri = "https://cards.scryfall.io/normal/front/b/5/b5f700c6-7591-481d-b223-21f5b820e831.jpg?1783922742"
    }
}
