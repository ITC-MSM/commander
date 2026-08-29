package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.effects.MayEffect
import com.wingedsheep.sdk.scripting.effects.Mode
import com.wingedsheep.sdk.scripting.effects.ModalEffect
import com.wingedsheep.sdk.scripting.effects.TapUntapEffect
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Merrow Reejerey
 * {2}{U}
 * Creature — Merfolk Soldier
 * 2/2
 * Other Merfolk creatures you control get +1/+1.
 * Whenever you cast a Merfolk spell, you may tap or untap target permanent.
 *
 * The Merfolk lord that also unlocks the tribe's tempo game: every Merfolk you cast taps a blocker
 * or untaps a land. The trigger fires on *cast*, so it resolves before the spell that caused it —
 * untapping the land that paid for it, and letting the next Merfolk come down the same turn.
 *
 * Reejerey itself is a Merfolk, so casting it does not trigger its own ability: the ability is not
 * on the battlefield yet while the spell is on the stack.
 */
val MerrowReejerey = card("Merrow Reejerey") {
    manaCost = "{2}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Merfolk Soldier"
    power = 2
    toughness = 2
    oracleText = "Other Merfolk creatures you control get +1/+1.\n" +
        "Whenever you cast a Merfolk spell, you may tap or untap target permanent."

    staticAbility {
        ability = ModifyStats(
            powerBonus = 1,
            toughnessBonus = 1,
            filter = GroupFilter(
                GameObjectFilter.Creature.withSubtype(Subtype.MERFOLK).youControl(),
                excludeSelf = true
            )
        )
    }

    triggeredAbility {
        trigger = Triggers.YouCastSubtype(Subtype.MERFOLK)
        val permanent = target("target permanent", Targets.Permanent)
        effect = MayEffect(
            ModalEffect(
                modes = listOf(
                    Mode.noTarget(TapUntapEffect(permanent, tap = true), "Tap that permanent"),
                    Mode.noTarget(TapUntapEffect(permanent, tap = false), "Untap that permanent")
                ),
                chooseCount = 1,
                countsAsModalSpell = false
            )
        )
        description = "Whenever you cast a Merfolk spell, you may tap or untap target permanent."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "74"
        artist = "Greg Staples"
        flavorText = "Steady and silent as the deep current, the reejerey guides the course of the school."
        imageUri = "https://cards.scryfall.io/normal/front/b/5/b57434d9-6fa5-43a6-98b9-044d2259d699.jpg?1783942900"
    }
}
