package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.Duration
import com.wingedsheep.sdk.scripting.TriggeredAbility
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect
import com.wingedsheep.sdk.scripting.effects.GrantTriggeredAbilityEffect
import com.wingedsheep.sdk.scripting.events.DamageType
import com.wingedsheep.sdk.scripting.values.ContextPropertyKey
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Flash Conscription
 * {5}{R}
 * Instant
 *
 * Untap target creature and gain control of it until end of turn. That creature gains haste until
 * end of turn. If {W} was spent to cast this spell, the creature gains "Whenever this creature
 * deals combat damage, you gain that much life" until end of turn.
 *
 * One of Ravnica's "if {X} was spent" riders: the card is mono-red and the white mana is a
 * *payment* question, not a colour requirement, so a Boros land or any-colour source turns it on.
 * `Conditions.ManaSpentToCastIncludes` reads the payment recorded on the spell, so a copy of Flash
 * Conscription — never cast, nothing spent for it — correctly misses the rider.
 *
 * The granted ability is the Vigorous Charge shape: a `GrantTriggeredAbilityEffect` carrying a
 * combat-damage trigger whose amount is the event's own damage. The life goes to the ability's
 * controller, i.e. whoever controls the creature when it connects — which is you for this turn,
 * and its owner again once the control effect wears off (2005-10-01 ruling).
 */
val FlashConscription = card("Flash Conscription") {
    manaCost = "{5}{R}"
    colorIdentity = "R"
    typeLine = "Instant"
    oracleText = "Untap target creature and gain control of it until end of turn. That creature " +
        "gains haste until end of turn. If {W} was spent to cast this spell, the creature gains " +
        "\"Whenever this creature deals combat damage, you gain that much life\" until end of turn."

    spell {
        val conscript = target("target creature", Targets.Creature)
        effect = Effects.Untap(conscript)
            .then(Effects.GainControl(conscript, Duration.EndOfTurn))
            .then(Effects.GrantKeyword(Keyword.HASTE, conscript))
            .then(
                ConditionalEffect(
                    condition = Conditions.ManaSpentToCastIncludes(requiredWhite = 1),
                    effect = GrantTriggeredAbilityEffect(
                        ability = TriggeredAbility.create(
                            trigger = Triggers.dealsDamage(damageType = DamageType.Combat).event,
                            binding = Triggers.dealsDamage(damageType = DamageType.Combat).binding,
                            effect = Effects.GainLife(
                                DynamicAmount.ContextProperty(ContextPropertyKey.TRIGGER_DAMAGE_AMOUNT)
                            ),
                            descriptionOverride = "Whenever this creature deals combat damage, you gain that much life."
                        ),
                        target = conscript,
                        duration = Duration.EndOfTurn
                    )
                )
            )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "124"
        artist = "Stephen Tappin"
        imageUri = "https://cards.scryfall.io/normal/front/5/2/52361237-a653-4470-9ed5-c531c040c686.jpg?1783943654"
        ruling(
            "2005-10-01",
            "If white mana was paid, the player who controls the creature when it deals combat damage " +
                "will be the one who gains life."
        )
    }
}
