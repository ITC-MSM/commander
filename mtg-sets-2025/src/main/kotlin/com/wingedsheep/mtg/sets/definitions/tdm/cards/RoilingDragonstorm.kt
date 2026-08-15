package com.wingedsheep.mtg.sets.definitions.tdm.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Roiling Dragonstorm — Tarkir: Dragonstorm #55
 * {1}{U} · Enchantment · Uncommon
 *
 * When this enchantment enters, draw two cards, then discard a card.
 * When a Dragon you control enters, return this enchantment to its owner's hand.
 *
 * Plain draw-then-discard ETB ([Effects.DrawCards] then [Effects.Discard]) plus the shared
 * Dragonstorm-cycle Dragon-bounce trigger ([Triggers.entersBattlefield] over Dragons you
 * control, [TriggerBinding.OTHER], returning this enchantment to hand).
 */
val RoilingDragonstorm = card("Roiling Dragonstorm") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Enchantment"
    oracleText = "When this enchantment enters, draw two cards, then discard a card.\n" +
        "When a Dragon you control enters, return this enchantment to its owner's hand."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.DrawCards(2) then Effects.Discard(1)
        description = "When this enchantment enters, draw two cards, then discard a card."
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
        collectorNumber = "55"
        artist = "Gaboleps"
        imageUri = "https://cards.scryfall.io/normal/front/4/5/455f4c96-684b-4b14-bd21-6799da2e1fa7.jpg?1743204180"
    }
}
