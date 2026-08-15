package com.wingedsheep.mtg.sets.definitions.ltr.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.effects.ModalEffect
import com.wingedsheep.sdk.scripting.effects.Mode
import com.wingedsheep.sdk.scripting.effects.ReflexiveTriggerEffect
import com.wingedsheep.sdk.scripting.events.DamageType
import com.wingedsheep.sdk.scripting.events.RecipientFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Gorbag of Minas Morgul
 * {1}{B}
 * Legendary Creature — Orc Soldier
 * 2/2
 * Whenever a Goblin or Orc you control deals combat damage to a player, you may sacrifice it.
 * When you do, choose one —
 * • Draw a card.
 * • Create a Treasure token.
 */
val GorbagOfMinasMorgul = card("Gorbag of Minas Morgul") {
    manaCost = "{1}{B}"
    colorIdentity = "B"
    typeLine = "Legendary Creature — Orc Soldier"
    power = 2
    toughness = 2
    oracleText = "Whenever a Goblin or Orc you control deals combat damage to a player, you may sacrifice it. When you do, choose one —\n• Draw a card.\n• Create a Treasure token. (It's an artifact with \"{T}, Sacrifice this token: Add one mana of any color.\")"

    triggeredAbility {
        trigger = Triggers.dealsDamage(
            damageType = DamageType.Combat,
            recipient = RecipientFilter.AnyPlayer,
            sourceFilter = (GameObjectFilter.Creature.withSubtype("Goblin") or
                GameObjectFilter.Creature.withSubtype("Orc")).youControl(),
            binding = TriggerBinding.ANY,
        )
        effect = ReflexiveTriggerEffect(
            action = Effects.SacrificeTarget(EffectTarget.TriggeringEntity),
            optional = true,
            reflexiveEffect = ModalEffect.chooseOne(
                Mode.noTarget(Effects.DrawCards(1), "Draw a card."),
                Mode.noTarget(Effects.CreateTreasure(), "Create a Treasure token.")
            )
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "86"
        artist = "Alex Brock"
        imageUri = "https://cards.scryfall.io/normal/front/5/8/58aafdb6-1c8c-4fc4-a52e-3e601be7fb0c.jpg?1686968472"
    }
}
