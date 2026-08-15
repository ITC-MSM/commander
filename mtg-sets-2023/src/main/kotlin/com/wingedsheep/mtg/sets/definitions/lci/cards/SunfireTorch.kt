package com.wingedsheep.mtg.sets.definitions.lci.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantTriggeredAbility
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.TriggeredAbility
import com.wingedsheep.sdk.scripting.effects.ReflexiveTriggerEffect
import com.wingedsheep.sdk.scripting.effects.SelectTargetEffect
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Sunfire Torch (LCI #167) — {R} Artifact — Equipment (common)
 *
 * Equipped creature gets +1/+0 and has "Whenever this creature attacks, you may sacrifice
 * Sunfire Torch. When you do, this creature deals 2 damage to any target."
 * Equip {1}
 *
 * Implementation notes:
 * - The +1/+0 buff is a [ModifyStats] static scoped to [Filters.EquippedCreature].
 * - The quoted attack ability is granted to the equipped creature via [GrantTriggeredAbility]
 *   + [Triggers.attacks] (SELF binding, the Pirate Hat / Dire Blunderbuss idiom), so it lives
 *   on the creature and fires when that creature attacks. The body is a [ReflexiveTriggerEffect]:
 *   the optional action sacrifices Sunfire Torch; only "when you do" puts the reflexive part on
 *   the stack to choose any target and deal 2 damage. The damage source defaults to the granted
 *   ability's source — the equipped creature — matching "this creature deals 2 damage."
 *
 * "sacrifice Sunfire Torch" — CR 201.5a: the name refers only to the specific granting
 * Equipment. A granted *triggered* ability resolves with the equipped creature as its source
 * (not the Equipment), so the sacrifice is scoped with `.attachedToSource()` to "the Sunfire
 * Torch attached to this creature" — i.e. the Equipment that granted the ability. With a second
 * Sunfire Torch elsewhere on the battlefield this correctly leaves it untouched (only the one
 * attached to the attacking creature is a legal sacrifice).
 */
val SunfireTorch = card("Sunfire Torch") {
    manaCost = "{R}"
    colorIdentity = "R"
    typeLine = "Artifact — Equipment"
    oracleText = "Equipped creature gets +1/+0 and has \"Whenever this creature attacks, you may " +
        "sacrifice Sunfire Torch. When you do, this creature deals 2 damage to any target.\"\n" +
        "Equip {1} ({1}: Attach to target creature you control. Equip only as a sorcery.)"

    // Equipped creature gets +1/+0.
    staticAbility {
        ability = ModifyStats(+1, +0, Filters.EquippedCreature)
    }

    // ... and has "Whenever this creature attacks, you may sacrifice Sunfire Torch. When you
    // do, this creature deals 2 damage to any target."
    staticAbility {
        ability = GrantTriggeredAbility(
            ability = TriggeredAbility.create(
                trigger = Triggers.attacks().event,
                binding = Triggers.attacks().binding,
                effect = ReflexiveTriggerEffect(
                    action = Effects.Composite(listOf(
                        SelectTargetEffect(
                            requirement = TargetObject(
                                filter = TargetFilter(
                                    GameObjectFilter.Artifact.named("Sunfire Torch").attachedToSource()
                                )
                            ),
                            storeAs = "toSacrifice"
                        ),
                        Effects.SacrificeTarget(EffectTarget.PipelineTarget("toSacrifice"))
                    )),
                    optional = true,
                    reflexiveEffect = Effects.DealDamage(2, EffectTarget.ContextTarget(0)),
                    reflexiveTargetRequirements = listOf(Targets.Any),
                    descriptionOverride = "You may sacrifice Sunfire Torch. When you do, this creature deals 2 damage to any target."
                )
            ),
            filter = Filters.EquippedCreature
        )
    }

    equipAbility("{1}")

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "167"
        artist = "David Szabo"
        flavorText = "\"Let Tilonalli's light burn a path through this darkness.\""
        imageUri = "https://cards.scryfall.io/normal/front/b/b/bb040fc7-f728-4482-92af-9a320c03bb54.jpg?1782694475"
    }
}
