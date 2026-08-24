package com.wingedsheep.mtg.sets.definitions.grn.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.TimingRule

/**
 * Boros Locket
 * {3}
 * Artifact
 * {T}: Add {R} or {W}.
 * {R/W}{R/W}{R/W}{R/W}, {T}, Sacrifice this artifact: Draw two cards.
 */
val BorosLocket = card("Boros Locket") {
    manaCost = "{3}"
    colorIdentity = "RW"
    typeLine = "Artifact"
    oracleText = "{T}: Add {R} or {W}.\n" +
        "{R/W}{R/W}{R/W}{R/W}, {T}, Sacrifice this artifact: Draw two cards."

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddMana(Color.RED)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }
    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddMana(Color.WHITE)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }
    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{R/W}{R/W}{R/W}{R/W}"), Costs.Tap, Costs.SacrificeSelf)
        effect = Effects.DrawCards(2)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "231"
        artist = "Aaron Miller"
        flavorText = "\"We pass these along to our fellow soldiers to recognize deeds of valor. It won't stay with you for long.\"\n—Alovnek, Boros guildmage"
        imageUri = "https://cards.scryfall.io/normal/front/5/e/5e972d97-0df2-44e4-8ff2-cc8707316dc1.jpg?1783934109"
    }
}
