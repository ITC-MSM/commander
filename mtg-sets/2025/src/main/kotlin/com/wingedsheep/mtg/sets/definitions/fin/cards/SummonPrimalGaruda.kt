package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Summon: Primal Garuda
 * {3}{W}
 * Enchantment Creature — Saga Harpy
 * 3/3
 * (As this Saga enters and after your draw step, add a lore counter. Sacrifice after III.)
 * I — Aerial Blast — This creature deals 4 damage to target tapped creature an opponent controls.
 * II, III — Slipstream — Another target creature you control gets +1/+0 and gains flying until
 *     end of turn.
 * Flying
 */
val SummonPrimalGaruda = card("Summon: Primal Garuda") {
    manaCost = "{3}{W}"
    colorIdentity = "W"
    typeLine = "Enchantment Creature — Saga Harpy"
    oracleText = "(As this Saga enters and after your draw step, add a lore counter. Sacrifice after III.)\n" +
        "I — Aerial Blast — This creature deals 4 damage to target tapped creature an opponent controls.\n" +
        "II, III — Slipstream — Another target creature you control gets +1/+0 and gains flying until end of turn.\n" +
        "Flying"
    power = 3
    toughness = 3

    keywords(Keyword.FLYING)

    sagaChapter(1) {
        // "This creature deals 4 damage …" — the saga-creature itself is the damage source (Self).
        val tapped = target(
            "creature",
            TargetObject(filter = TargetFilter(GameObjectFilter.Creature.opponentControls().tapped())),
        )
        effect = Effects.DealDamage(4, tapped, damageSource = EffectTarget.Self)
    }

    sagaChapter(2) {
        val ally = target("creature", TargetObject(filter = TargetFilter.OtherCreatureYouControl))
        effect = Effects.Composite(
            Effects.ModifyStats(1, 0, ally),
            Effects.GrantKeyword(Keyword.FLYING, ally),
        )
    }

    sagaChapter(3) {
        val ally = target("creature", TargetObject(filter = TargetFilter.OtherCreatureYouControl))
        effect = Effects.Composite(
            Effects.ModifyStats(1, 0, ally),
            Effects.GrantKeyword(Keyword.FLYING, ally),
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "37"
        artist = "Ryan Valle"
        imageUri = "https://cards.scryfall.io/normal/front/e/4/e44497a8-067e-454e-a9c0-684f03df55ff.jpg?1748705892"
    }
}
