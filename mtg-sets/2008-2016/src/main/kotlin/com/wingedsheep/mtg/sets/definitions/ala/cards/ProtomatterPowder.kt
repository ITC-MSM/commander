package com.wingedsheep.mtg.sets.definitions.ala.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Protomatter Powder
 * {2}{U}
 * Artifact
 * {4}{W}, {T}, Sacrifice this artifact: Return target artifact card from your graveyard to the battlefield.
 *
 * The three printed cost atoms compose through [Costs.Composite] — [Costs.Mana], [Costs.Tap] and
 * [Costs.SacrificeSelf] — and the target is a [TargetObject] over
 * [TargetFilter.ArtifactInYourGraveyard], the same shape Sharuum the
 * Hegemon uses. The recursion is [Effects.PutOntoBattlefieldFromGraveyard], the `fromZone`-guarded
 * sibling of `PutOntoBattlefield`, so the move is skipped if the card has left the graveyard by the
 * time the ability resolves.
 */
val ProtomatterPowder = card("Protomatter Powder") {
    manaCost = "{2}{U}"
    colorIdentity = "UW"
    typeLine = "Artifact"
    oracleText = "{4}{W}, {T}, Sacrifice this artifact: Return target artifact card from your graveyard to the battlefield."

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{4}{W}"), Costs.Tap, Costs.SacrificeSelf)
        val t = target(
            "target",
            TargetObject(
                filter = TargetFilter.ArtifactInYourGraveyard
            )
        )
        effect = Effects.PutOntoBattlefieldFromGraveyard(t)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "53"
        artist = "Francis Tsai"
        flavorText = "\"There is no such thing as scrap metal. All such material can be repaired with the proper bonding agent.\"\n—Quennus, metallurgeon"
        imageUri = "https://cards.scryfall.io/normal/front/2/7/273e2198-5549-4e82-8580-8769284d729d.jpg"
    }
}
