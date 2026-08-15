package com.wingedsheep.mtg.sets.definitions.dsk.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.scripting.Duration
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.enduring
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Enduring Courage
 * {2}{R}{R}
 * Enchantment Creature — Dog Glimmer
 * 3/3
 * Whenever another creature you control enters, it gets +2/+0 and gains haste until end of turn.
 * When Enduring Courage dies, if it was a creature, return it to the battlefield under its
 *   owner's control. It's an enchantment. (It's not a creature.)
 *
 * The death clause is the Duskmourn "Enduring" mechanic — see [enduring].
 */
val EnduringCourage = card("Enduring Courage") {
    manaCost = "{2}{R}{R}"
    colorIdentity = "R"
    typeLine = "Enchantment Creature — Dog Glimmer"
    oracleText = "Whenever another creature you control enters, it gets +2/+0 and gains haste " +
        "until end of turn.\n" +
        "When Enduring Courage dies, if it was a creature, return it to the battlefield under " +
        "its owner's control. It's an enchantment. (It's not a creature.)"
    power = 3
    toughness = 3

    enduring()

    triggeredAbility {
        trigger = Triggers.entersBattlefield(
            filter = GameObjectFilter.Creature.youControl(),
            binding = TriggerBinding.OTHER
        )
        effect = Effects.Composite(
            Effects.ModifyStats(2, 0, EffectTarget.TriggeringEntity, Duration.EndOfTurn),
            Effects.GrantKeyword(Keyword.HASTE, EffectTarget.TriggeringEntity, Duration.EndOfTurn)
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "133"
        artist = "Yigit Koroglu"
        imageUri = "https://cards.scryfall.io/normal/front/f/4/f46ac55f-d68e-4d5d-af0a-3879f97f705e.jpg?1726286344"
    }
}
