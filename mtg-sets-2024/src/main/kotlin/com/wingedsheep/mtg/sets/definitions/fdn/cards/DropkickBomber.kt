package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.TriggeredAbility
import com.wingedsheep.sdk.scripting.Duration
import com.wingedsheep.sdk.scripting.effects.GrantTriggeredAbilityEffect
import com.wingedsheep.sdk.scripting.effects.SacrificeSelfEffect
import com.wingedsheep.sdk.scripting.events.DamageType
import com.wingedsheep.sdk.scripting.events.RecipientFilter
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Dropkick Bomber
 * {2}{R}
 * Creature — Goblin Warrior
 * 2/3
 *
 * Other Goblins you control get +1/+1.
 * {R}: Until end of turn, another target Goblin you control gains flying and "When this
 * creature deals combat damage, sacrifice it."
 *
 * The anthem is a [ModifyStats] static over other Goblins you control (excludeSelf). The
 * activated ability grants two things to another target Goblin until end of turn: flying
 * ([Effects.GrantKeyword]) and a granted triggered ability ([GrantTriggeredAbilityEffect])
 * whose SELF-bound combat-damage trigger (to any recipient — player or creature) sacrifices
 * the creature it was granted to ([SacrificeSelfEffect] resolves against the trigger's own
 * source). "Another target" excludes Dropkick Bomber itself via [TargetFilter.excludeSelf].
 */
val DropkickBomber = card("Dropkick Bomber") {
    manaCost = "{2}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Goblin Warrior"
    power = 2
    toughness = 3
    oracleText = "Other Goblins you control get +1/+1.\n" +
        "{R}: Until end of turn, another target Goblin you control gains flying and \"When this " +
        "creature deals combat damage, sacrifice it.\""

    staticAbility {
        ability = ModifyStats(
            powerBonus = 1,
            toughnessBonus = 1,
            filter = GroupFilter(
                GameObjectFilter.Creature.withSubtype(Subtype.GOBLIN).youControl(),
                excludeSelf = true
            )
        )
    }

    activatedAbility {
        cost = Costs.Mana("{R}")
        val goblin = target(
            "another target Goblin you control",
            TargetCreature(
                filter = TargetFilter(
                    GameObjectFilter.Creature.withSubtype(Subtype.GOBLIN).youControl(),
                    excludeSelf = true
                )
            )
        )
        effect = Effects.Composite(
            Effects.GrantKeyword(Keyword.FLYING, goblin, Duration.EndOfTurn),
            GrantTriggeredAbilityEffect(
                ability = TriggeredAbility.create(
                    trigger = Triggers.dealsDamage(
                        damageType = DamageType.Combat,
                        recipient = RecipientFilter.Any,
                        binding = TriggerBinding.SELF
                    ).event,
                    binding = TriggerBinding.SELF,
                    effect = SacrificeSelfEffect,
                    descriptionOverride = "When this creature deals combat damage, sacrifice it."
                ),
                target = goblin,
                duration = Duration.EndOfTurn
            )
        )
        description = "Until end of turn, another target Goblin you control gains flying and " +
            "\"When this creature deals combat damage, sacrifice it.\""
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "537"
        artist = "Quintin Gleim"
        flavorText = "\"I've always enjoyed kicking stuff, so this was an ideal career opportunity.\""
        imageUri = "https://cards.scryfall.io/normal/front/b/4/b44f758e-716a-408e-96d4-b58403591c2a.jpg?1782688798"
    }
}
