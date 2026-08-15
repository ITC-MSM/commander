package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.effects.MayEffect

/**
 * Rook Turret
 * {3}{U}
 * Artifact Creature — Construct
 * 3/3
 * Flying
 * Whenever another artifact you control enters, you may draw a card. If you do, discard a card.
 */
val RookTurret = card("Rook Turret") {
    manaCost = "{3}{U}"
    colorIdentity = "U"
    typeLine = "Artifact Creature — Construct"
    power = 3
    toughness = 3
    oracleText = "Flying\nWhenever another artifact you control enters, you may draw a card. If you do, discard a card."

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.entersBattlefield(
            filter = GameObjectFilter.Artifact.youControl(),
            binding = TriggerBinding.OTHER
        )
        // "you may draw a card. If you do, discard a card." — loot coupled draw+discard, gated by MayEffect.
        effect = MayEffect(Patterns.Hand.loot())
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "69"
        artist = "Thanh Tuấn"
        flavorText = "\"'Tis unfortunate that many Ishgardians cannot appreciate the practicality of my work.\"\n—Stephanivien de Haillenarte"
        imageUri = "https://cards.scryfall.io/normal/front/4/5/4572884d-0c0e-41e7-b219-f76b95fdbd01.jpg?1748706014"
    }
}
