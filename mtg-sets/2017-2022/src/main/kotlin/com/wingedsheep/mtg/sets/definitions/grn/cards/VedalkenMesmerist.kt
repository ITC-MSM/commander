package com.wingedsheep.mtg.sets.definitions.grn.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Vedalken Mesmerist
 * {1}{U}
 * Creature — Vedalken Wizard
 * 2/1
 * Whenever this creature attacks, target creature an opponent controls gets -2/-0 until end of turn.
 */
val VedalkenMesmerist = card("Vedalken Mesmerist") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Vedalken Wizard"
    oracleText = "Whenever this creature attacks, target creature an opponent controls gets -2/-0 until end of turn."
    power = 2
    toughness = 1

    triggeredAbility {
        trigger = Triggers.Attacks
        val creature = target("target", Targets.CreatureOpponentControls)
        effect = Effects.ModifyStats(-2, 0, creature)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "57"
        artist = "Zezhou Chen"
        flavorText = "\"There's no need to sound the alarm. You are minding your post admirably. I am authorized. All is well.\""
        imageUri = "https://cards.scryfall.io/normal/front/5/b/5b5e0a12-2589-473b-90e4-1ee5acc055a2.jpg?1783934181"
    }
}
