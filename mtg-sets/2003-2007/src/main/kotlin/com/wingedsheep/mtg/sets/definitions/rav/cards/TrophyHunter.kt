package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Trophy Hunter
 * {2}{G}
 * Creature — Human Archer
 * 2/3
 * {1}{G}: This creature deals 1 damage to target creature with flying.
 * Whenever a creature with flying dealt damage by this creature this turn dies, put a +1/+1
 * counter on this creature.
 *
 * The second ability's two halves are independent: the damage may have come from either the
 * activated ability or combat, and the dying creature need not have been killed by it. Flying is
 * checked against the creature's last-known information as it left the battlefield.
 */
val TrophyHunter = card("Trophy Hunter") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Human Archer"
    oracleText = "{1}{G}: This creature deals 1 damage to target creature with flying.\n" +
        "Whenever a creature with flying dealt damage by this creature this turn dies, put a " +
        "+1/+1 counter on this creature."
    power = 2
    toughness = 3

    // Centaur Archer's shape: "with flying" is a targeting predicate, and the damage source is the
    // ability's own source, which is Effects.DealDamage's default.
    activatedAbility {
        cost = Costs.Mana("{1}{G}")
        val t = target("target", Targets.CreatureWithKeyword(Keyword.FLYING))
        effect = Effects.DealDamage(1, t)
    }

    triggeredAbility {
        trigger = Triggers.creatureDealtDamageByThisDies(
            GameObjectFilter.Creature.withKeyword(Keyword.FLYING)
        )
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "187"
        artist = "rk post"
        imageUri = "https://cards.scryfall.io/normal/front/f/1/f18d1047-9010-437c-94ab-7a8ad3a0250f.jpg?1783943629"
        ruling(
            "2005-10-01",
            "The second ability checks whether a creature being put into a graveyard (a) currently has " +
                "flying and (b) was dealt damage earlier this turn by Trophy Hunter. It doesn't matter " +
                "whether Trophy Hunter dealt the damage that caused the creature to be destroyed, whether " +
                "the damage from Trophy Hunter was dealt in combat or via its activated ability, or who " +
                "controlled the creature."
        )
    }
}
