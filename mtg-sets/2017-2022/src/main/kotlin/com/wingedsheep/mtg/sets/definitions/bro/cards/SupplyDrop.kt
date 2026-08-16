package com.wingedsheep.mtg.sets.definitions.bro.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Supply Drop
 * {3}
 * Artifact
 * Flash
 * When this artifact enters, target creature you control gets +2/+2 until end of turn.
 * {4}, {T}, Sacrifice this artifact: Draw a card.
 */
val SupplyDrop = card("Supply Drop") {
    manaCost = "{3}"
    typeLine = "Artifact"
    oracleText = "Flash\n" +
        "When this artifact enters, target creature you control gets +2/+2 until end of turn.\n" +
        "{4}, {T}, Sacrifice this artifact: Draw a card."

    keywords(Keyword.FLASH)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val t = target("target", TargetCreature(filter = TargetFilter.CreatureYouControl))
        effect = Effects.ModifyStats(2, 2, t)
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{4}"), Costs.Tap, Costs.SacrificeSelf)
        effect = Effects.DrawCards(1)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "250"
        artist = "Brian Valeza"
        flavorText = "New armaments, fresh provisions, and best of all, dry socks."
        imageUri = "https://cards.scryfall.io/normal/front/8/2/82a5d3e9-bada-448b-894d-9d4e7e0463c7.jpg?1783920011"
    }
}
