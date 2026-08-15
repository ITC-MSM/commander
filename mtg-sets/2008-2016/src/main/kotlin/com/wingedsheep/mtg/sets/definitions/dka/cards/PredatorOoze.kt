package com.wingedsheep.mtg.sets.definitions.dka.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Predator Ooze
 * {G}{G}{G}
 * Creature — Ooze
 * 1/1
 * Indestructible (Damage and effects that say "destroy" don't destroy this creature.)
 * Whenever this creature attacks, put a +1/+1 counter on it.
 * Whenever a creature dealt damage by this creature this turn dies, put a +1/+1 counter on this creature.
 *
 * The third ability uses last-known information: it checks whether Predator Ooze had dealt damage to the
 * dying creature at any point this turn, regardless of who controlled it or whose graveyard it went to.
 */
val PredatorOoze = card("Predator Ooze") {
    manaCost = "{G}{G}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Ooze"
    oracleText = "Indestructible (Damage and effects that say \"destroy\" don't destroy this creature.)\n" +
        "Whenever this creature attacks, put a +1/+1 counter on it.\n" +
        "Whenever a creature dealt damage by this creature this turn dies, put a +1/+1 counter on this creature."
    power = 1
    toughness = 1

    keywords(Keyword.INDESTRUCTIBLE)

    triggeredAbility {
        trigger = Triggers.Attacks
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.Self)
    }

    triggeredAbility {
        trigger = Triggers.CreatureDealtDamageByThisDies
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "124"
        artist = "Ryan Yee"
        imageUri = "https://cards.scryfall.io/normal/front/7/3/73c71457-f7c9-4ab4-b89d-e235e3f15e16.jpg?1562922371"
        ruling(
            "2011-01-22",
            "Each time a creature dies, check whether Predator Ooze had dealt any damage to it at any " +
                "time during that turn. If so, Predator Ooze's ability will trigger. It doesn't matter who " +
                "controlled the creature or whose graveyard it was put into."
        )
    }
}
