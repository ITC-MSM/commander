package com.wingedsheep.mtg.sets.definitions.ala.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetPermanent

/**
 * Filigree Sages
 * {3}{U}
 * Artifact Creature — Vedalken Wizard
 * 2 / 3
 * {2}{U}: Untap target artifact.
 *
 * A single mana-only activated ability — no tap in the cost, so it can be used repeatedly and on
 * the Sages themselves. [Effects.Untap] over a [TargetPermanent] filtered to artifacts; the
 * battlefield filter reads projected state, so a permanent that is only *currently* an artifact
 * (an animated Vedalken Shackles, say) is a legal target.
 */
val FiligreeSages = card("Filigree Sages") {
    manaCost = "{3}{U}"
    colorIdentity = "U"
    typeLine = "Artifact Creature — Vedalken Wizard"
    power = 2
    toughness = 3
    oracleText = "{2}{U}: Untap target artifact."

    activatedAbility {
        cost = Costs.Mana("{2}{U}")
        val t = target("target", TargetPermanent(filter = TargetFilter.Artifact))
        effect = Effects.Untap(t)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "44"
        artist = "Dan Murayama Scott"
        flavorText = "\"We of the Sanctum Arcanum have pondered every word on every page of the Filigree Texts. If you can't say the same, don't bother speaking.\""
        imageUri = "https://cards.scryfall.io/normal/front/0/8/08790aaf-0142-4b20-89cf-cdaffeea4582.jpg"
    }
}
