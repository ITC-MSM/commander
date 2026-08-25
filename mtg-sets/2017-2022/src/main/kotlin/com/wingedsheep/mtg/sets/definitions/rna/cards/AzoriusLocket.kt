package com.wingedsheep.mtg.sets.definitions.rna.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.TimingRule

/**
 * Azorius Locket — Ravnica Allegiance #231
 * {3} · Artifact
 *
 * The RNA half of the Locket cycle, wired exactly like GRN's five: "Add {W} or {U}" is two
 * separate mana abilities rather than one choice effect, because each is independently
 * activatable and the engine's mana enumeration walks abilities, not choices. The draw is a
 * normal activated ability (it does not produce mana, so it uses the stack).
 */
val AzoriusLocket = card("Azorius Locket") {
    manaCost = "{3}"
    colorIdentity = "UW"
    typeLine = "Artifact"
    oracleText = "{T}: Add {W} or {U}.\n" +
        "{W/U}{W/U}{W/U}{W/U}, {T}, Sacrifice this artifact: Draw two cards."

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddMana(Color.WHITE)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }
    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddMana(Color.BLUE)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }
    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{W/U}{W/U}{W/U}{W/U}"), Costs.Tap, Costs.SacrificeSelf)
        effect = Effects.DrawCards(2)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "231"
        artist = "Craig J Spearing"
        flavorText = "\"Mandatory lockets enable the tracking of all Senate personnel for improved security and efficiency.\"\n" +
        "—Dovin Baan"
        imageUri = "https://cards.scryfall.io/normal/front/1/3/13aed078-9e29-48e7-b145-5252362031a0.jpg"
    }
}
