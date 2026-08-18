package com.wingedsheep.mtg.sets.definitions.mkm.cards

import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.effects.Gate
import com.wingedsheep.sdk.scripting.effects.GatedEffect
import com.wingedsheep.sdk.scripting.effects.MayPlayExpiry
import com.wingedsheep.sdk.scripting.events.RecipientFilter
import com.wingedsheep.sdk.scripting.values.ContextPropertyKey
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Expedited Inheritance
 * {R}{R}
 * Enchantment
 * Whenever a creature is dealt damage, its controller may exile that many cards from the top of
 * their library. They may play those cards until the end of their next turn.
 */
val ExpeditedInheritance = card("Expedited Inheritance") {
    manaCost = "{R}{R}"
    colorIdentity = "R"
    typeLine = "Enchantment"
    oracleText = "Whenever a creature is dealt damage, its controller may exile that many cards " +
        "from the top of their library. They may play those cards until the end of their next turn."

    triggeredAbility {
        trigger = Triggers.dealsDamage(
            recipient = RecipientFilter.AnyCreature,
            binding = TriggerBinding.ANY,
        )
        // Damage triggers bind the recipient as the triggering entity, so this makes the damaged
        // creature's controller own both the trigger and the ensuing may decision.
        controlledByTriggeringEntityController = true
        effect = GatedEffect(
            gate = Gate.MayDecide(),
            then = Patterns.Exile.impulse(
                count = DynamicAmount.ContextProperty(ContextPropertyKey.TRIGGER_DAMAGE_AMOUNT),
                expiry = MayPlayExpiry.UntilEndOfNextTurn,
            ),
            descriptionOverride = "You may exile that many cards from the top of your library. " +
                "You may play those cards until the end of your next turn.",
        )
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "123"
        artist = "Micah Epstein"
        flavorText = "Astronomical wealth hinders peaceful succession."
        imageUri = "https://cards.scryfall.io/normal/front/b/6/b65209da-cf48-4d37-b045-7d181070fd05.jpg?1783912881"

        ruling(
            "2024-02-02",
            "Players pay all costs and follow all normal timing rules for cards played from exile " +
                "with Expedited Inheritance's permission. For example, if one of the exiled cards " +
                "is a land card, they may play it only during their main phase while the stack is empty.",
        )
    }
}
