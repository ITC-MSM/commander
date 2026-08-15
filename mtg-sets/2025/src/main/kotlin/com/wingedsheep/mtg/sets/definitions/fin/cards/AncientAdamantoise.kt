package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.DamagePersistsThroughCleanup
import com.wingedsheep.sdk.scripting.EventPattern
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.RedirectDamage
import com.wingedsheep.sdk.scripting.events.RecipientFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Ancient Adamantoise
 * {5}{G}{G}{G}
 * Creature — Turtle
 * 8/20
 * Vigilance, ward {3}
 * Damage isn't removed from this creature during cleanup steps.
 * All damage that would be dealt to you and other permanents you control is dealt to this
 * creature instead.
 * When this creature dies, exile it and create ten tapped Treasure tokens.
 *
 * The redirection is modelled as two static [RedirectDamage] replacements — one for damage to
 * the controller ([RecipientFilter.You]) and one for damage to their permanents
 * ([RecipientFilter.Matching] over `Permanent.youControl()`). Both redirect to [EffectTarget.Self].
 * Damage already headed to the Adamantoise itself is left alone (the engine skips a redirect
 * whose destination equals the original recipient, CR-faithful "other permanents").
 *
 * Because it soaks damage from the whole board, the marked damage must persist so it can
 * eventually become lethal — hence [DamagePersistsThroughCleanup], an exception to the CR 514.2
 * cleanup damage-removal turn-based action. Per the card's ruling, other damage-removal effects
 * (regeneration, "remove all damage") are unaffected; only the cleanup step is suppressed.
 *
 * The dies trigger functions from the graveyard (`triggerZone = Zone.GRAVEYARD`) so "exile it"
 * can reference [EffectTarget.Self].
 */
val AncientAdamantoise = card("Ancient Adamantoise") {
    manaCost = "{5}{G}{G}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Turtle"
    power = 8
    toughness = 20
    oracleText = "Vigilance, ward {3}\n" +
        "Damage isn't removed from this creature during cleanup steps.\n" +
        "All damage that would be dealt to you and other permanents you control is dealt to " +
        "this creature instead.\n" +
        "When this creature dies, exile it and create ten tapped Treasure tokens."

    keywords(Keyword.VIGILANCE, Keyword.WARD)
    keywordAbility(KeywordAbility.ward("{3}"))

    staticAbility {
        ability = DamagePersistsThroughCleanup
    }

    // "All damage that would be dealt to you ... is dealt to this creature instead."
    replacementEffect(
        RedirectDamage(
            redirectTo = EffectTarget.Self,
            appliesTo = EventPattern.DamageEvent(recipient = RecipientFilter.You),
        )
    )
    // "... and other permanents you control is dealt to this creature instead."
    replacementEffect(
        RedirectDamage(
            redirectTo = EffectTarget.Self,
            appliesTo = EventPattern.DamageEvent(
                recipient = RecipientFilter.Matching(GameObjectFilter.Permanent.youControl()),
            ),
        )
    )

    triggeredAbility {
        trigger = Triggers.Dies
        triggerZone = Zone.GRAVEYARD
        effect = Effects.Composite(
            Effects.Exile(EffectTarget.Self),
            Effects.CreateTreasure(count = 10, tapped = true)
        )
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "172"
        artist = "Kevin Glint"
        imageUri = "https://cards.scryfall.io/normal/front/4/c/4c139f30-5ecd-48fd-ae7c-ec2cc98889ff.jpg?1782686472"
        ruling("2025-06-06", "If you control more than one Ancient Adamantoise, you choose which redirection effect to apply. You can't divide damage dealt by a single source between them, and you can't choose to take the damage yourself.")
        ruling("2025-06-06", "Effects that remove all damage from a permanent (such as regeneration) will still remove damage from Ancient Adamantoise.")
    }
}
