package com.wingedsheep.mtg.sets.definitions.tdm.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Teeming Dragonstorm — Tarkir: Dragonstorm #30
 * {3}{W} · Enchantment · Uncommon
 *
 * When this enchantment enters, create two 2/2 white Soldier creature tokens.
 * When a Dragon you control enters, return this enchantment to its owner's hand.
 *
 * ETB makes two fixed 2/2 white Soldier tokens ([Effects.CreateToken]). Shares the
 * Dragonstorm-cycle Dragon-bounce trigger.
 */
val TeemingDragonstorm = card("Teeming Dragonstorm") {
    manaCost = "{3}{W}"
    colorIdentity = "W"
    typeLine = "Enchantment"
    oracleText = "When this enchantment enters, create two 2/2 white Soldier creature tokens.\n" +
        "When a Dragon you control enters, return this enchantment to its owner's hand."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.CreateToken(
            count = 2,
            power = 2,
            toughness = 2,
            colors = setOf(Color.WHITE),
            creatureTypes = setOf("Soldier"),
            imageUri = "https://cards.scryfall.io/normal/front/7/d/7ddd5153-2d16-4a4e-b9bc-f20313d322da.jpg?1743176203"
        )
        description = "When this enchantment enters, create two 2/2 white Soldier creature tokens."
    }

    triggeredAbility {
        trigger = Triggers.entersBattlefield(
            filter = GameObjectFilter.Creature.youControl().withSubtype(Subtype.DRAGON),
            binding = TriggerBinding.OTHER
        )
        effect = Effects.ReturnToHand(EffectTarget.Self)
        description = "When a Dragon you control enters, return this enchantment to its owner's hand."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "30"
        artist = "Leon Tukker"
        imageUri = "https://cards.scryfall.io/normal/front/3/b/3b9d771f-24dc-4ed6-8051-62df576a2ba5.jpg?1743204080"
    }
}
