package com.wingedsheep.mtg.sets.definitions.ice.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Fyndhorn Brownie
 * {2}{G}
 * Creature — Ouphe
 * 1/1
 *
 * {2}{G}, {T}: Untap target creature.
 *
 * Seeker of Skybreak with a mana cost bolted on: the printed comma is a [Costs.Composite] in mana
 * then tap order, and the untap is the shared `TapUntap` primitive via [Effects.Untap].
 */
val FyndhornBrownie = card("Fyndhorn Brownie") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Ouphe"
    power = 1
    toughness = 1
    oracleText = "{2}{G}, {T}: Untap target creature."

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{2}{G}"), Costs.Tap)
        val t = target("target", Targets.Creature)
        effect = Effects.Untap(t)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "242"
        artist = "Richard Thomas"
        flavorText = "\"I've been insulted by drunks in a hundred inns, but never as skillfully or annoyingly as by those blasted Brownies.\"\n—General Jarkeld, the Arctic Fox"
        imageUri = "https://cards.scryfall.io/normal/front/0/6/06204e82-9dfd-4334-a23a-f8240fc37772.jpg"
    }
}
