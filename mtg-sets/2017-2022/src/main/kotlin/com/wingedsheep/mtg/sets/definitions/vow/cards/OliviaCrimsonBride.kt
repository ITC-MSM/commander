package com.wingedsheep.mtg.sets.definitions.vow.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.Duration
import com.wingedsheep.sdk.scripting.StateTriggeredAbility
import com.wingedsheep.sdk.scripting.effects.GrantStateTriggeredAbilityEffect
import com.wingedsheep.sdk.scripting.effects.ZonePlacement
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Olivia, Crimson Bride
 * {4}{B}{R}
 * Legendary Creature — Vampire Noble
 * 3/4
 *
 * Flying, haste
 * Whenever Olivia attacks, return target creature card from your graveyard to the battlefield
 * tapped and attacking. It gains "When you don't control a legendary Vampire, exile this creature."
 *
 * The rider is a **state-triggered ability** (CR 603.8), not an event trigger: nothing *happens*
 * when the last legendary Vampire leaves — the reanimated creature's condition simply starts being
 * true, and the check is a poll at every priority pass. That is why this card grants a
 * [StateTriggeredAbility] via [GrantStateTriggeredAbilityEffect] rather than a `TriggeredAbility`:
 * a "whenever Olivia dies" trigger would be wrong three ways (Olivia leaving the battlefield by
 * exile or bounce fires nothing, a *second* legendary Vampire on board should keep the creature
 * alive, and the rider has to keep working for the rest of the game, not just this turn).
 *
 * Two details the ordering and the filter carry:
 *
 *  - **The grant follows the Move.** `ZoneTransitionService` wipes an entity's granted abilities on
 *    battlefield entry (CR 400.7 — the returning card is a new object), so granting before the
 *    return would silently drop the rider. The `Effects.Composite` order is load-bearing.
 *  - **No `excludeSelf` on the condition.** A reanimated legendary Vampire sees *itself*, so Olivia
 *    bringing back, say, another legendary Vampire leaves it self-sustaining once Olivia is gone —
 *    which is what the printed wording says.
 *
 * The grant is [Duration.Permanent]: the rider is a durable property of the returned creature, and
 * the printed text names no end point.
 */
val OliviaCrimsonBride = card("Olivia, Crimson Bride") {
    manaCost = "{4}{B}{R}"
    colorIdentity = "BR"
    typeLine = "Legendary Creature — Vampire Noble"
    power = 3
    toughness = 4
    oracleText = "Flying, haste\n" +
        "Whenever Olivia attacks, return target creature card from your graveyard to the " +
        "battlefield tapped and attacking. It gains \"When you don't control a legendary Vampire, " +
        "exile this creature.\""

    keywords(Keyword.FLYING, Keyword.HASTE)

    triggeredAbility {
        trigger = Triggers.Attacks
        val creature = target(
            "creature card from your graveyard",
            TargetObject(filter = TargetFilter.CreatureInYourGraveyard)
        )
        effect = Effects.Composite(
            Effects.Move(
                target = creature,
                destination = Zone.BATTLEFIELD,
                placement = ZonePlacement.TappedAndAttacking,
                fromZone = Zone.GRAVEYARD
            ),
            GrantStateTriggeredAbilityEffect(
                ability = StateTriggeredAbility.create(
                    condition = Conditions.YouControl(
                        GameObjectFilter.Creature.legendary().withSubtype(Subtype.VAMPIRE),
                        negate = true
                    ),
                    effect = Effects.Exile(EffectTarget.Self),
                    descriptionOverride =
                        "When you don't control a legendary Vampire, exile this creature."
                ),
                target = creature,
                duration = Duration.Permanent
            )
        )
        description = "Whenever Olivia attacks, return target creature card from your graveyard " +
            "to the battlefield tapped and attacking. It gains \"When you don't control a " +
            "legendary Vampire, exile this creature.\""
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "245"
        artist = "Anna Steinbauer"
        flavorText = "Her grand wedding had one goal: to unite the vampire bloodlines under her rule."
        imageUri = "https://cards.scryfall.io/normal/front/3/0/301dacc9-ef92-4515-b907-a70d6c3fd73e.jpg?1783924788"
    }
}
