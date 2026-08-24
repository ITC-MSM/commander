package com.wingedsheep.mtg.sets.definitions.bfz.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.MayPayManaEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Murasa Ranger
 * {3}{G}
 * Creature — Human Warrior Ranger
 * 3/3
 * Landfall — Whenever a land you control enters, you may pay {3}{G}. If you do, put two +1/+1 counters on this creature.
 *
 * "You may pay {3}{G}. If you do" is the flat [MayPayManaEffect] gate — a mana
 * [com.wingedsheep.sdk.scripting.effects.Gate.MayPay] with no `otherwise`, which is the
 * shape the engine recognises for manual mana-source selection at resolution.
 */
val MurasaRanger = card("Murasa Ranger") {
    manaCost = "{3}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Human Warrior Ranger"
    power = 3
    toughness = 3
    oracleText = "Landfall — Whenever a land you control enters, you may pay {3}{G}. If you do, put two +1/+1 " +
        "counters on this creature."

    triggeredAbility {
        trigger = Triggers.LandYouControlEnters
        effect = MayPayManaEffect(
            cost = ManaCost.parse("{3}{G}"),
            effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 2, EffectTarget.Self),
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "178"
        artist = "Eric Deschamps"
        flavorText = "\"If you're not prepared to fight, you'd best be prepared to die.\""
        imageUri = "https://cards.scryfall.io/normal/front/c/4/c4a3c02b-b576-4099-9016-15449f93bb09.jpg?1783938187"
    }
}
