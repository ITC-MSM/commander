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
 * Tajuru Warcaller
 * {3}{G}{G}
 * Creature — Elf Warrior Ally
 * 2/1
 * Rally — Whenever this creature or another Ally you control enters, creatures you control get +2/+2 until end of turn.
 *
 * Rally is an ability word, not a keyword: the trigger is an ANY-bound enters trigger over
 * Allies you control, so the creature's own arrival fires it alongside every later Ally.
 */
val TajuruWarcaller = card("Tajuru Warcaller") {
    manaCost = "{3}{G}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Elf Warrior Ally"
    power = 2
    toughness = 1
    oracleText = "Rally — Whenever this creature or another Ally you control enters, creatures you control get " +
        "+2/+2 until end of turn."

    triggeredAbility {
        trigger = Triggers.entersBattlefield(
            filter = GameObjectFilter.Permanent.withSubtype("Ally").youControl(),
            binding = TriggerBinding.ANY,
        )
        effect = Patterns.Group.modifyStatsForAll(2, 2, Filters.Group.creaturesYouControl)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "195"
        artist = "Anastasia Ovchinnikova"
        flavorText = "Her rallying cry reaches far beyond the Tajuru elves, inspiring all those who hear it."
        imageUri = "https://cards.scryfall.io/normal/front/7/6/762501fe-5102-4c21-8ad5-430590a59830.jpg?1783938183"
    }
}
