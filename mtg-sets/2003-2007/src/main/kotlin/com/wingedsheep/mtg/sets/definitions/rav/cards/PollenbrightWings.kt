package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.effects.CreateTokenEffect
import com.wingedsheep.sdk.scripting.events.DamageType
import com.wingedsheep.sdk.scripting.events.RecipientFilter
import com.wingedsheep.sdk.scripting.values.ContextPropertyKey
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Pollenbright Wings
 * {4}{G}{W}
 * Enchantment — Aura
 * Enchant creature
 * Enchanted creature has flying.
 * Whenever enchanted creature deals combat damage to a player, create that many 1/1 green
 * Saproling creature tokens.
 *
 * "That many" is the damage the *enchanted* creature dealt, so the trigger is the ordinary
 * combat-damage-to-a-player event read one object out — [TriggerBinding.ATTACHED] — and the
 * token count is [ContextPropertyKey.TRIGGER_DAMAGE_AMOUNT], the same payload
 * [com.wingedsheep.mtg.sets.definitions.ons.cards.BroodhatchNantuko] counts its Insects with.
 * The tokens are created under the Aura's controller, not the enchanted creature's — that is
 * `CreateTokenEffect`'s default, and it is what makes stealing a creature with this Aura work.
 */
val PollenbrightWings = card("Pollenbright Wings") {
    manaCost = "{4}{G}{W}"
    colorIdentity = "GW"
    typeLine = "Enchantment — Aura"
    oracleText = "Enchant creature\n" +
        "Enchanted creature has flying.\n" +
        "Whenever enchanted creature deals combat damage to a player, create that many 1/1 green " +
        "Saproling creature tokens."

    auraTarget = Targets.Creature

    staticAbility {
        ability = GrantKeyword(Keyword.FLYING)
    }

    triggeredAbility {
        trigger = Triggers.dealsDamage(
            damageType = DamageType.Combat,
            recipient = RecipientFilter.AnyPlayer,
            binding = TriggerBinding.ATTACHED,
        )
        effect = CreateTokenEffect(
            count = DynamicAmount.ContextProperty(ContextPropertyKey.TRIGGER_DAMAGE_AMOUNT),
            power = 1,
            toughness = 1,
            colors = setOf(Color.GREEN),
            creatureTypes = setOf("Saproling"),
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "219"
        artist = "Terese Nielsen"
        imageUri = "https://cards.scryfall.io/normal/front/3/c/3ca4f3b9-c952-4fd3-a586-3e063b62b23d.jpg"
    }
}
