package com.wingedsheep.mtg.sets.definitions.grn.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.TimingRule

/**
 * Selesnya Locket
 * {3}
 * Artifact
 * {T}: Add {G} or {W}.
 * {G/W}{G/W}{G/W}{G/W}, {T}, Sacrifice this artifact: Draw two cards.
 */
val SelesnyaLocket = card("Selesnya Locket") {
    manaCost = "{3}"
    colorIdentity = "GW"
    typeLine = "Artifact"
    oracleText = "{T}: Add {G} or {W}.\n" +
        "{G/W}{G/W}{G/W}{G/W}, {T}, Sacrifice this artifact: Draw two cards."

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddMana(Color.GREEN)
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
        cost = Costs.Composite(Costs.Mana("{G/W}{G/W}{G/W}{G/W}"), Costs.Tap, Costs.SacrificeSelf)
        effect = Effects.DrawCards(2)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "240"
        artist = "Winona Nelson"
        flavorText = "\"Think of the locket as a seed you bear, spreading life from Vitu-Ghazi across all of Ravnica.\"\n—Heruj, Selesnya hierophant"
        imageUri = "https://cards.scryfall.io/normal/front/e/a/ea0c04b9-c7fc-4204-9af2-5d1987bdd97e.jpg?1783934106"
    }
}
