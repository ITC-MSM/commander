package com.wingedsheep.mtg.sets.definitions.tmt.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetPermanent

/**
 * Dimensional Exile
 * {1}{W}
 * Enchantment — Aura
 *
 * Enchant basic land you control
 * When this Aura enters, exile target creature an opponent controls
 * until this Aura leaves the battlefield.
 */
val DimensionalExile = card("Dimensional Exile") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Enchantment — Aura"
    oracleText = "Enchant basic land you control\nWhen this Aura enters, exile target creature an opponent controls until this Aura leaves the battlefield."

    auraTarget = TargetPermanent(filter = TargetFilter(GameObjectFilter.BasicLand.youControl()))

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val creature = target(
            "creature an opponent controls",
            TargetPermanent(filter = TargetFilter.Creature.opponentControls())
        )
        effect = Effects.ExileUntilLeaves(creature)
    }

    triggeredAbility {
        trigger = Triggers.LeavesBattlefield
        effect = Effects.ReturnLinkedExileUnderOwnersControl()
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "4"
        artist = "Lordigan"
        flavorText = "\"Never been defeated, huh? Well, you never tangled with a turtle before!\"\n—Donatello"
        imageUri = "https://cards.scryfall.io/normal/front/b/e/be8d96fb-a1be-4fff-b844-e38d185884e1.jpg?1771342164"
    }
}
