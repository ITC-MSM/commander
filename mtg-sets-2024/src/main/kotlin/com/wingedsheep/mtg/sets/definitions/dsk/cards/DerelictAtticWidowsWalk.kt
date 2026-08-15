package com.wingedsheep.mtg.sets.definitions.dsk.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.CardLayout
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.events.AttackPredicate
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Derelict Attic // Widow's Walk (DSK 93) — split-layout Room (CR 709.5).
 *
 * Derelict Attic {2}{B} — Enchantment — Room
 *   When you unlock this door, you draw two cards and you lose 2 life.
 *
 * Widow's Walk {3}{B} — Enchantment — Room
 *   Whenever a creature you control attacks alone, it gets +1/+0 and gains deathtouch
 *   until end of turn.
 *
 * Cast each half separately; the cast face enters unlocked, the other locked. Pay the locked
 * face's printed mana cost as a sorcery-speed special action to unlock it (CR 709.5e).
 *
 * Derelict Attic is a "when you unlock this door" trigger (CR 709.5h) — draw two, lose 2 life,
 * both affecting the controller. Widow's Walk reuses the attacks-alone primitive
 * [Triggers.attacks] with `AttackPredicate.Alone` and an ANY binding over creatures you control,
 * buffing the lone attacker ("it" = [EffectTarget.TriggeringEntity]) with +1/+0 and deathtouch
 * until end of turn.
 */
val DerelictAtticWidowsWalk = card("Derelict Attic // Widow's Walk") {
    layout = CardLayout.SPLIT
    colorIdentity = "B"

    face("Derelict Attic") {
        manaCost = "{2}{B}"
        typeLine = "Enchantment — Room"
        oracleText = "When you unlock this door, you draw two cards and you lose 2 life."

        triggeredAbility {
            trigger = Triggers.OnDoorUnlocked
            effect = Effects.Composite(
                Effects.DrawCards(2),
                Effects.LoseLife(2, EffectTarget.Controller),
            )
        }
    }

    face("Widow's Walk") {
        manaCost = "{3}{B}"
        typeLine = "Enchantment — Room"
        oracleText = "Whenever a creature you control attacks alone, it gets +1/+0 and gains " +
            "deathtouch until end of turn."

        triggeredAbility {
            trigger = Triggers.attacks(
                filter = GameObjectFilter.Creature.youControl(),
                requires = setOf(AttackPredicate.Alone),
                binding = TriggerBinding.ANY,
            )
            effect = Effects.Composite(
                Effects.ModifyStats(1, 0, EffectTarget.TriggeringEntity),
                Effects.GrantKeyword(Keyword.DEATHTOUCH, EffectTarget.TriggeringEntity),
            )
        }
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "93"
        artist = "Marc Simonetti"
        imageUri = "https://cards.scryfall.io/normal/front/2/c/2cae24c1-53f1-4f3f-8795-b634c46a17c4.jpg?1726780598"
    }
}
