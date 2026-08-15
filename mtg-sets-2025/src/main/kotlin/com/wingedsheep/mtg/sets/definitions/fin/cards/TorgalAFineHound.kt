package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Torgal, A Fine Hound
 * {1}{G}
 * Legendary Creature — Wolf
 * 2/2
 *
 * Whenever you cast your first Human creature spell each turn, that creature enters with an
 * additional +1/+1 counter on it for each Dog and/or Wolf you control.
 * {T}: Add one mana of any color.
 *
 * Implementation notes:
 *  - "your first Human creature spell each turn" = a [Triggers.youCastSpell] trigger filtered to
 *    Human creature spells (`GameObjectFilter.Creature.withSubtype("Human")`) with
 *    `oncePerTurn = true`, so it fires on the first such spell you cast each turn and not again.
 *  - The counter is placed on the triggering spell while it is still on the stack
 *    ([EffectTarget.TriggeringEntity]); it travels with the spell to the battlefield as it
 *    resolves, satisfying "that creature enters with an additional +1/+1 counter on it"
 *    naturally — the same primitive Summon: Fenrir's chapter II uses, here with a dynamic count.
 *  - "for each Dog and/or Wolf you control" is a dynamic amount counting permanents you control
 *    that are Dogs or Wolves (`withAnySubtype("Dog", "Wolf")`, OR logic). Counting zero is fine —
 *    the spell still resolves; it just enters with no extra counters. (Torgal himself is a Wolf,
 *    so unless he has left the battlefield the count is at least 1.)
 *  - "{T}: Add one mana of any color" is a standard mana ability (`manaAbility = true`).
 */
val TorgalAFineHound = card("Torgal, A Fine Hound") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Legendary Creature — Wolf"
    oracleText = "Whenever you cast your first Human creature spell each turn, that creature " +
        "enters with an additional +1/+1 counter on it for each Dog and/or Wolf you control.\n" +
        "{T}: Add one mana of any color."
    power = 2
    toughness = 2

    triggeredAbility {
        trigger = Triggers.youCastSpell(
            spellFilter = GameObjectFilter.Creature.withSubtype("Human"),
        )
        oncePerTurn = true
        effect = Effects.AddDynamicCounters(
            Counters.PLUS_ONE_PLUS_ONE,
            DynamicAmounts.battlefield(
                Player.You,
                GameObjectFilter.Creature.withAnySubtype("Dog", "Wolf"),
            ).count(),
            EffectTarget.TriggeringEntity,
        )
    }

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddAnyColorMana(1)
        manaAbility = true
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "208"
        artist = "Narendra Bintara Adi"
        flavorText = "\"Thank you, Torgal. For never giving up.\"\n—Clive Rosfield"
        imageUri = "https://cards.scryfall.io/normal/front/0/f/0f5725aa-42bb-4dfd-9c15-135b38b33da3.jpg?1748706543"
    }
}
