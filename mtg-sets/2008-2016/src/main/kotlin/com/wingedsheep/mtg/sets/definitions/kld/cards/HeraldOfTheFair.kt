package com.wingedsheep.mtg.sets.definitions.kld.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Herald of the Fair
 * {2}{W}
 * Creature — Human
 * 3/2
 * When this creature enters, target creature you control gets +1/+1 until end of turn.
 *
 * The pump may legally target the Herald itself — the trigger is not restricted to "another"
 * creature — so the target requirement is a plain [TargetFilter.CreatureYouControl].
 */
val HeraldOfTheFair = card("Herald of the Fair") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human"
    oracleText = "When this creature enters, target creature you control gets +1/+1 until end of turn."
    power = 3
    toughness = 2

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val t = target("target", TargetCreature(filter = TargetFilter.CreatureYouControl))
        effect = Effects.ModifyStats(1, 1, t)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "18"
        artist = "Steven Belledin"
        flavorText = "\"Welcome, one and all! Right this way! Open your eyes, your ears, and your imaginations to the wonders of the Inventors' Fair!\""
        imageUri = "https://cards.scryfall.io/normal/front/c/e/ce9ed217-8378-4a58-a00d-fa4e4cb27c9d.jpg?1783937232"
    }
}
