package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.effects.MayEffect
import com.wingedsheep.sdk.scripting.effects.MustBeBlockedEffect
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Magitek Scythe
 * {4}
 * Artifact — Equipment
 * A Test of Your Reflexes! — When this Equipment enters, you may attach it to target creature you
 *   control. If you do, that creature gains first strike until end of turn and must be blocked this
 *   turn if able.
 * Equipped creature gets +2/+1.
 * Equip {2}
 *
 * The ETB is "you may attach … if you do, …": the target creature you control is chosen when the
 * trigger goes on the stack (CR 603.3d), then on resolution the controller may decline. Accepting
 * runs the whole [MayEffect] body — attach, grant first strike until end of turn, and force the
 * creature to be blocked this turn. "Must be blocked … if able" is the at-least-one-blocker form
 * ([MustBeBlockedEffect] with `allCreatures = false`), not the Lure-style every-blocker form.
 */
val MagitekScythe = card("Magitek Scythe") {
    manaCost = "{4}"
    typeLine = "Artifact — Equipment"
    oracleText = "A Test of Your Reflexes! — When this Equipment enters, you may attach it to target " +
        "creature you control. If you do, that creature gains first strike until end of turn and " +
        "must be blocked this turn if able.\n" +
        "Equipped creature gets +2/+1.\n" +
        "Equip {2}"

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val t = target("target", TargetCreature(filter = TargetFilter.Creature.youControl()))
        effect = MayEffect(
            Effects.Composite(
                Effects.AttachEquipment(t),
                Effects.GrantKeyword(Keyword.FIRST_STRIKE, t),
                MustBeBlockedEffect(t, allCreatures = false)
            )
        )
    }

    staticAbility {
        ability = ModifyStats(2, 1)
    }

    equipAbility("{2}")

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "562"
        artist = "Thanh Tuấn"
        imageUri = "https://cards.scryfall.io/normal/front/8/b/8b691d42-3498-4d47-9a46-f7c376df8886.jpg?1748707605"
    }
}
