package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Genji Glove
 * {5}
 * Artifact — Equipment
 * Equipped creature has double strike.
 * Whenever equipped creature attacks, if it's the first combat phase of the turn, untap it.
 *   After this phase, there is an additional combat phase.
 * Equip {3}
 *
 * Mirrors the Raph & Leo / Éomer shape: attack trigger with an anti-infinite-loop limiter,
 * untap the attacker, then [Effects.AddCombatPhase] to add a second combat phase (combat only,
 * no trailing main phase). The trigger binds to the attached creature via [TriggerBinding.ATTACHED],
 * and "untap it" untaps that same equipped creature via [EffectTarget.EquippedCreature].
 *
 * **Approximation note:** the printed rider is *"if it's the first combat phase of the turn"*
 * (intervening-if), but the engine has no "first combat phase" condition today. The same
 * anti-infinite-loop function is served by `oncePerTurn = true`; the two diverge only in a narrow
 * edge case (another source of additional combat exists *and* the equipped creature first attacks
 * in combat #2 — the intervening-if would fail, `oncePerTurn = true` would still let it fire once).
 * When a `Conditions.IsFirstCombatPhase` primitive lands, swap `oncePerTurn = true` for a faithful
 * `triggerCondition`.
 */
val GenjiGlove = card("Genji Glove") {
    manaCost = "{5}"
    colorIdentity = ""
    typeLine = "Artifact — Equipment"
    oracleText = "Equipped creature has double strike.\n" +
        "Whenever equipped creature attacks, if it's the first combat phase of the turn, untap it. After this phase, there is an additional combat phase.\n" +
        "Equip {3}"

    staticAbility {
        ability = GrantKeyword(Keyword.DOUBLE_STRIKE, Filters.EquippedCreature)
    }

    triggeredAbility {
        trigger = Triggers.attacks(binding = TriggerBinding.ATTACHED)
        oncePerTurn = true
        effect = Effects.Composite(
            listOf(
                // "untap it" — the attacking equipped creature.
                Effects.Untap(EffectTarget.EquippedCreature),
                // "After this phase, there is an additional combat phase." (combat only — no main)
                Effects.AddCombatPhase,
            )
        )
    }

    equipAbility("{3}")

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "258"
        artist = "Elizabeth Peiró"
        flavorText = "\"I must say, I quite enjoy these tussles.\""
        imageUri = "https://cards.scryfall.io/normal/front/f/7/f724dde1-84b0-4e3b-a9b8-44cd22bb9f79.jpg?1748706757"
    }
}
