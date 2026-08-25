package com.wingedsheep.mtg.sets.definitions.rna.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TimingRule

/**
 * Tome of the Guildpact — Ravnica Allegiance #242
 * {5} · Artifact — Book
 *
 * "Whenever you cast a multicolored spell" is [Triggers.youCastSpell] narrowed by
 * [GameObjectFilter.Multicolored] — the multicolour test reads the spell's *colors*, so a
 * hybrid card counts only when it actually has two or more. The mana ability is unrestricted
 * any-colour fixing.
 */
val TomeOfTheGuildpact = card("Tome of the Guildpact") {
    manaCost = "{5}"
    colorIdentity = ""
    typeLine = "Artifact — Book"
    oracleText = "Whenever you cast a multicolored spell, draw a card.\n" +
        "{T}: Add one mana of any color."

    triggeredAbility {
        trigger = Triggers.youCastSpell(spellFilter = GameObjectFilter.Multicolored)
        effect = Effects.DrawCards(1)
    }
    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddManaOfChoice()
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "242"
        artist = "Randy Gallegos"
        flavorText = "\"Reading it has given me a glimpse of what makes this deeply flawed city so very magnificent.\"\n" +
        "—Dovin Baan"
        imageUri = "https://cards.scryfall.io/normal/front/9/5/95e307d4-7e5f-4f00-869e-da0e7abbf27f.jpg"
    }
}
