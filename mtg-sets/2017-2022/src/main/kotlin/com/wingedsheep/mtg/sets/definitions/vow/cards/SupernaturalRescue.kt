package com.wingedsheep.mtg.sets.definitions.vow.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.predicates.ControllerPredicate
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Supernatural Rescue
 * {3}{W}
 * Enchantment — Aura
 *
 * This spell has flash as long as you control a Spirit.
 * When you cast this spell, tap up to two target creatures you don't control.
 * Enchant creature you control
 * Enchanted creature gets +1/+2.
 *
 * `conditionalFlash` is the Colossal Rattlewurm rail: a timing permission read while the card is
 * still in hand, so it is a `Condition` on the card rather than a keyword or a static ability. The
 * printed noun is a bare "Spirit", which names a *permanent* with that subtype rather than a
 * creature with it — the reading a Spirit land or a Spirit enchanted to be noncreature would
 * separate, and the one Assay's bare-subtype rule takes.
 *
 * The tap is a **cast** trigger ([Triggers.WhenYouCastThisSpell]), not an ETB one — it goes on the
 * stack above the Aura and resolves first, so the creatures are tapped even if the Aura itself is
 * countered or its enchant target has gone away.
 *
 * "Creatures you don't control" is `Not(ControlledByYou)`, not `opponentControls()`. The two are
 * the same in a duel and differ in multiplayer, where a teammate's creature is one you don't
 * control but not one an opponent controls — and this card is legal in Two-Headed Giant.
 */
val SupernaturalRescue = card("Supernatural Rescue") {
    manaCost = "{3}{W}"
    colorIdentity = "W"
    typeLine = "Enchantment — Aura"
    oracleText = "This spell has flash as long as you control a Spirit.\n" +
        "When you cast this spell, tap up to two target creatures you don't control.\n" +
        "Enchant creature you control\n" +
        "Enchanted creature gets +1/+2."

    conditionalFlash = Conditions.YouControl(
        GameObjectFilter.Permanent.withSubtype(Subtype.SPIRIT)
    )

    auraTarget = Targets.CreatureYouControl

    triggeredAbility {
        trigger = Triggers.WhenYouCastThisSpell()
        target(
            "up to two target creatures you don't control",
            TargetCreature(
                optional = true,
                count = 2,
                filter = TargetFilter(
                    GameObjectFilter.Creature.withControllerPredicate(
                        ControllerPredicate.Not(ControllerPredicate.ControlledByYou)
                    )
                )
            )
        )
        effect = Effects.TapEachTarget()
        description = "When you cast this spell, tap up to two target creatures you don't control."
    }

    staticAbility {
        ability = ModifyStats(1, 2)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "37"
        artist = "Anastasia Ovchinnikova"
        imageUri = "https://cards.scryfall.io/normal/front/9/6/960ca69c-916d-4af3-82a4-5f6fb614d6a4.jpg?1783924908"
    }
}
