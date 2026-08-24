package com.wingedsheep.mtg.sets.definitions.bfz.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding

/**
 * Tajuru Beastmaster
 * {5}{G}
 * Creature — Elf Warrior Ally
 * 5/5
 * Rally — Whenever this creature or another Ally you control enters, creatures you control get +1/+1 until end of turn.
 *
 * Rally is an ability word, not a keyword: the trigger is an ANY-bound enters trigger over
 * Allies you control, so the creature's own arrival fires it alongside every later Ally.
 */
val TajuruBeastmaster = card("Tajuru Beastmaster") {
    manaCost = "{5}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Elf Warrior Ally"
    power = 5
    toughness = 5
    oracleText = "Rally — Whenever this creature or another Ally you control enters, creatures you control get " +
        "+1/+1 until end of turn."

    triggeredAbility {
        trigger = Triggers.entersBattlefield(
            filter = GameObjectFilter.Permanent.withSubtype("Ally").youControl(),
            binding = TriggerBinding.ANY,
        )
        effect = Patterns.Group.modifyStatsForAll(1, 1, Filters.Group.creaturesYouControl)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "193"
        artist = "Greg Opalinski"
        flavorText = "\"My mount needs neither reins nor bridle. We both share a common purpose.\""
        imageUri = "https://cards.scryfall.io/normal/front/3/4/3447b4af-2eb2-48ae-a12e-2fdf83d4cb70.jpg?1783938184"
    }
}
