package com.wingedsheep.mtg.sets.definitions.chk.cards

import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantTriggeredAbility
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.TriggeredAbility
import com.wingedsheep.sdk.scripting.effects.CreateDelayedTriggerEffect
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Sosuke, Son of Seshiro
 * {2}{G}{G}
 * Legendary Creature — Snake Warrior
 * 3/4
 *
 * Other Snake creatures you control get +1/+0.
 * Whenever a Warrior you control deals combat damage to a creature, destroy that creature at end of
 * combat.
 *
 * The lord line prints "Snake **creatures**", so it filters on [GameObjectFilter.Creature] with
 * `excludeSelf` for the printed "Other"; the trigger's bare "a Warrior you control" is any permanent
 * with the subtype, hence [GameObjectFilter.Permanent] there.
 *
 * The granted ability is the Serpentine Basilisk shape: the deathtouch-at-a-delay is a
 * [CreateDelayedTriggerEffect] at [Step.END_COMBAT] destroying [EffectTarget.TriggeringEntity] — the
 * creature that was dealt the damage — rather than an immediate destroy, so a creature that leaves
 * combat or gains indestructible in the meantime behaves correctly.
 */
val SosukeSonOfSeshiro = card("Sosuke, Son of Seshiro") {
    manaCost = "{2}{G}{G}"
    colorIdentity = "G"
    typeLine = "Legendary Creature — Snake Warrior"
    power = 3
    toughness = 4
    oracleText = "Other Snake creatures you control get +1/+0.\n" +
        "Whenever a Warrior you control deals combat damage to a creature, destroy that creature " +
        "at end of combat."

    staticAbility {
        ability = ModifyStats(
            powerBonus = 1,
            toughnessBonus = 0,
            filter = GroupFilter(
                GameObjectFilter.Creature.withSubtype(Subtype.SNAKE).youControl(),
                excludeSelf = true
            )
        )
    }

    staticAbility {
        ability = GrantTriggeredAbility(
            ability = TriggeredAbility.create(
                trigger = Triggers.DealsCombatDamageToCreature.event,
                binding = Triggers.DealsCombatDamageToCreature.binding,
                effect = CreateDelayedTriggerEffect(
                    step = Step.END_COMBAT,
                    effect = Effects.Destroy(EffectTarget.TriggeringEntity)
                )
            ),
            filter = GroupFilter(GameObjectFilter.Permanent.withSubtype(Subtype.WARRIOR).youControl())
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "244"
        artist = "Carl Critchlow"
        imageUri = "https://cards.scryfall.io/normal/front/d/1/d1aa2451-e4f0-423b-826e-ae1f93b99e07.jpg?1783944282"
    }
}
