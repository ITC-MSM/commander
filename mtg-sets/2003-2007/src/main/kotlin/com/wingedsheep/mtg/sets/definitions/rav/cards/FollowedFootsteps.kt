package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Followed Footsteps
 * {3}{U}{U}
 * Enchantment — Aura
 *
 * Enchant creature
 * At the beginning of your upkeep, create a token that's a copy of enchanted creature.
 *
 * The copy is read at *resolution*, not when the trigger went on the stack — hence
 * [EffectTarget.EnchantedCreature] rather than a captured entity: if the Aura has moved to a
 * different creature in between, you get a copy of the newly-enchanted one (ruling of
 * 2007-02-01).
 */
val FollowedFootsteps = card("Followed Footsteps") {
    manaCost = "{3}{U}{U}"
    colorIdentity = "U"
    typeLine = "Enchantment — Aura"
    oracleText = "Enchant creature\n" +
        "At the beginning of your upkeep, create a token that's a copy of enchanted creature."

    auraTarget = Targets.Creature

    triggeredAbility {
        trigger = Triggers.YourUpkeep
        effect = Effects.CreateTokenCopyOfTarget(EffectTarget.EnchantedCreature)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "51"
        artist = "Glen Angus"
        flavorText = "Æther can turn a footprint into a blueprint."
        imageUri = "https://cards.scryfall.io/normal/front/d/0/d00e2fcc-0fa2-451e-8c69-7a24ba533e0b.jpg?1783943685"
        ruling(
            "2005-10-01",
            "Any \"enters\" abilities of the copied creature trigger when the creature tokens " +
                "enter the battlefield. The creature tokens also have any \"this enters with\" " +
                "or \"as this enters\" abilities that the copied creature has."
        )
        ruling(
            "2007-02-01",
            "If Followed Footsteps moves after it has triggered, you get a copy of the " +
                "newly-enchanted creature."
        )
    }
}
