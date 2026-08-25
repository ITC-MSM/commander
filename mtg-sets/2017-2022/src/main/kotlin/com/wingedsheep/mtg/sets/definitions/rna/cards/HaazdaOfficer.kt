package com.wingedsheep.mtg.sets.definitions.rna.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Haazda Officer — Ravnica Allegiance #10
 * {2}{W} · Creature — Human Soldier · 3 / 2
 *
 * An enters-trigger pump on a creature you control.
 */
val HaazdaOfficer = card("Haazda Officer") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Soldier"
    power = 3
    toughness = 2
    oracleText = "When this creature enters, target creature you control gets +1/+1 until end of turn."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val ally = target("target", Targets.CreatureYouControl)
        effect = Effects.ModifyStats(1, 1, ally)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "10"
        artist = "Aaron Miller"
        flavorText = "\"You two, cover the alley! You, with me! Eyes on windows, balconies, and rooftops. Who knows what a fish-octopus-crab can do!\""
        imageUri = "https://cards.scryfall.io/normal/front/5/b/5ba5f096-c6ea-4db6-966b-617e3454813f.jpg"
    }
}
