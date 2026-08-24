package com.wingedsheep.mtg.sets.definitions.ice.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Juniper Order Druid
 * {2}{G}
 * Creature — Human Cleric Druid
 * 1/1
 *
 * {T}: Untap target land.
 *
 * Ley Druid's ability verbatim: [Costs.Tap] and [Effects.Untap] — the shared `TapUntap` primitive
 * with `tap = false` — onto [Targets.Land].
 */
val JuniperOrderDruid = card("Juniper Order Druid") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Human Cleric Druid"
    power = 1
    toughness = 1
    oracleText = "{T}: Untap target land."

    activatedAbility {
        cost = Costs.Tap
        val t = target("target", Targets.Land)
        effect = Effects.Untap(t)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "251"
        artist = "Jeff A. Menges"
        flavorText = "\"The filthy towns of Kjeldor are no place for anyone to live. Fyndhorn is our home now.\"\n—Kolbjörn, Elder Druid of the Juniper Order"
        imageUri = "https://cards.scryfall.io/normal/front/c/b/cb211704-ff8e-498b-b7bb-f8384f198ffd.jpg"
    }
}
