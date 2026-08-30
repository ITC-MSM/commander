package com.wingedsheep.mtg.sets.definitions.avr.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Narstad Scrapper
 * {5}
 * Artifact Creature — Construct
 * 3 / 3
 *
 * {2}: This creature gets +1/+0 until end of turn.
 *
 * A bare mana-cost firebreathing-style pump: [Effects.ModifyStats] on [EffectTarget.Self], whose
 * default duration is already until end of turn.
 */
val NarstadScrapper = card("Narstad Scrapper") {
    manaCost = "{5}"
    typeLine = "Artifact Creature — Construct"
    power = 3
    toughness = 3
    oracleText = "{2}: This creature gets +1/+0 until end of turn."

    activatedAbility {
        cost = Costs.Mana("{2}")
        effect = Effects.ModifyStats(1, 0, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "218"
        artist = "Steven Belledin"
        flavorText = "\"Finally, the principles of corpse animation applied to bloodless materials!\"\n—Ludevic, necro-alchemist"
        imageUri = "https://cards.scryfall.io/normal/front/f/8/f808ed9b-95ac-4069-bdca-b100bc816b5b.jpg?1783940654"
    }
}
