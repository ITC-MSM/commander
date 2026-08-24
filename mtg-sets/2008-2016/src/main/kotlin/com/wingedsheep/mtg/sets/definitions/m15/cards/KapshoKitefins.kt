package com.wingedsheep.mtg.sets.definitions.m15.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding

/**
 * Kapsho Kitefins
 * {4}{U}{U}
 * Creature — Fish
 * 3/3
 * Flying
 * Whenever this creature or another creature you control enters, tap target creature an opponent controls.
 *
 * "This creature **or another**" is [TriggerBinding.ANY] — the Kitefins' own arrival triggers it too.
 */
val KapshoKitefins = card("Kapsho Kitefins") {
    manaCost = "{4}{U}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Fish"
    power = 3
    toughness = 3
    oracleText =
        "Flying\n" +
        "Whenever this creature or another creature you control enters, tap target creature an opponent controls."

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.entersBattlefield(
            filter = GameObjectFilter.Creature.youControl(),
            binding = TriggerBinding.ANY
        )
        val t = target("target creature an opponent controls", Targets.CreatureOpponentControls)
        effect = Effects.Tap(t)
        description = "Whenever this creature or another creature you control enters, tap target creature an opponent controls."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "66"
        artist = "Ryan Yee"
        flavorText = "\"It's a truly disconcerting sight to see their shadows cast upon the deck.\"\n—Captain Triff"
        imageUri = "https://cards.scryfall.io/normal/front/a/9/a964c5ed-b0ed-47cb-a0f0-79d28ef7f70b.jpg?1783939190"
    }
}
