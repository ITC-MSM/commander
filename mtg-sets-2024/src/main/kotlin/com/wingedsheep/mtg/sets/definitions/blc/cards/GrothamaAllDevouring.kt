package com.wingedsheep.mtg.sets.definitions.blc.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantTriggeredAbility
import com.wingedsheep.sdk.scripting.TriggeredAbility
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Grothama, All-Devouring {3}{G}{G}
 * Legendary Creature — Wurm
 * 10/8
 *
 * Other creatures have "Whenever this creature attacks, you may have it fight
 * Grothama, All-Devouring."
 * When Grothama leaves the battlefield, each player draws cards equal to the
 * amount of damage dealt to Grothama this turn by sources they controlled.
 *
 * The "have it fight Grothama" granted ability is modeled with a `TargetCreature`
 * requirement filtered by name — the oracle text names Grothama literally, so the
 * granted ability targets a creature named "Grothama, All-Devouring". The optional
 * `may` clause is honored by the engine's standard target-skip path: when the
 * controller of the granted trigger declines to choose, the trigger resolves with
 * no effect.
 *
 * The LTB draw effect reads a new per-source-controller damage tracker on Grothama
 * (captured as last-known info on the leave-battlefield event). See
 * `DamageDealtByPlayersThisTurnComponent` and
 * `EachPlayerDrawsForDamageDealtToSourceEffect` in the engine.
 */
val GrothamaAllDevouring = card("Grothama, All-Devouring") {
    manaCost = "{3}{G}{G}"
    colorIdentity = "G"
    typeLine = "Legendary Creature — Wurm"
    power = 10
    toughness = 8
    oracleText = "Other creatures have \"Whenever this creature attacks, you may have it " +
        "fight Grothama, All-Devouring.\"\n" +
        "When Grothama, All-Devouring leaves the battlefield, each player draws cards " +
        "equal to the amount of damage dealt to Grothama this turn by sources they controlled."

    val grothamaTarget = TargetCreature(
        filter = TargetFilter(GameObjectFilter.Creature.named("Grothama, All-Devouring"))
    )

    staticAbility {
        ability = GrantTriggeredAbility(
            ability = TriggeredAbility.create(
                trigger = Triggers.Attacks.event,
                binding = Triggers.Attacks.binding,
                effect = Effects.Fight(EffectTarget.Self, EffectTarget.ContextTarget(0)),
                optional = true,
                targetRequirement = grothamaTarget,
            ),
            filter = GroupFilter.AllCreatures.other(),
        )
    }

    triggeredAbility {
        trigger = Triggers.LeavesBattlefield
        effect = Effects.EachPlayerDrawsForDamageDealtToSource()
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "224"
        artist = "Filip Burburan"
        imageUri = "https://cards.scryfall.io/normal/front/0/8/08b5301b-9f3c-4fb2-a308-a47a16c08fc0.jpg?1721429302"
    }
}
