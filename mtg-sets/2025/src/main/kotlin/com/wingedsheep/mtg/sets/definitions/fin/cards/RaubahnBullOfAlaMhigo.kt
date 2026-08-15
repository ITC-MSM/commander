package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetPermanent

/**
 * Raubahn, Bull of Ala Mhigo
 * {1}{R}
 * Legendary Creature — Human Warrior
 * 2/2
 * Ward—Pay life equal to Raubahn's power.
 * Whenever Raubahn attacks, attach up to one target Equipment you control to target attacking
 *   creature.
 *
 * The ward cost is a dynamic life cost: [KeywordAbility.wardLife] with
 * [DynamicAmounts.sourcePower] models "Pay life equal to Raubahn's power". The amount is read
 * when the ward trigger resolves (CR 702.21b) — Raubahn's projected power then, or his
 * last-known power if he has already left the battlefield (CR 112.7a; Scryfall ruling
 * 2025-06-06). See [com.wingedsheep.sdk.scripting.effects.WardCost.DynamicLife].
 *
 * The attack trigger reuses [Effects.AttachTargetEquipmentToCreature]: the Equipment is "up to
 * one target" (optional — declining or an illegal attach is a no-op, Scryfall ruling
 * 2025-06-06), the attacking creature is a required target and may be Raubahn himself.
 */
val RaubahnBullOfAlaMhigo = card("Raubahn, Bull of Ala Mhigo") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Legendary Creature — Human Warrior"
    power = 2
    toughness = 2
    oracleText = "Ward—Pay life equal to Raubahn's power.\n" +
        "Whenever Raubahn attacks, attach up to one target Equipment you control to target " +
        "attacking creature."

    keywords(Keyword.WARD)
    keywordAbility(KeywordAbility.wardLife(DynamicAmounts.sourcePower()))

    triggeredAbility {
        trigger = Triggers.Attacks
        val equipment = target(
            "up to one target Equipment you control",
            TargetPermanent(
                filter = TargetFilter(
                    baseFilter = GameObjectFilter.Artifact.withSubtype(Subtype.EQUIPMENT).youControl()
                ),
                optional = true
            )
        )
        val creature = target("target attacking creature", Targets.AttackingCreature)
        effect = Effects.AttachTargetEquipmentToCreature(equipment, creature)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "151"
        artist = "Julia Vasilyeva"
        flavorText = "\"Come, then! Who will be next to die on my steel!?\""
        imageUri = "https://cards.scryfall.io/normal/front/7/0/7035d11b-525f-4120-8dcb-610095196681.jpg?1748706327"
    }
}
