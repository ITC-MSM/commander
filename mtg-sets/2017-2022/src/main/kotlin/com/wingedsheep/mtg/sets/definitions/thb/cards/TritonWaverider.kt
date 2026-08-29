package com.wingedsheep.mtg.sets.definitions.thb.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Triton Waverider
 * {3}{U}
 * Creature — Merfolk Wizard
 * 3/3
 *
 * Constellation — Whenever an enchantment you control enters, this creature gains flying until end of turn.
 *
 * Constellation is an ability word with no rules meaning of its own — a plain enters-the-battlefield
 * watcher over enchantments you control, bound with [TriggerBinding.ANY] because the Waverider is
 * not the permanent being watched. Same shape as Favored of Iroas, with flying in place of double
 * strike.
 */
val TritonWaverider = card("Triton Waverider") {
    manaCost = "{3}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Merfolk Wizard"
    power = 3
    toughness = 3
    oracleText = "Constellation — Whenever an enchantment you control enters, this creature gains flying until end of turn."

    triggeredAbility {
        trigger = Triggers.entersBattlefield(
            filter = GameObjectFilter.Enchantment.youControl(),
            binding = TriggerBinding.ANY,
        )
        effect = Effects.GrantKeyword(Keyword.FLYING, EffectTarget.Self)
        description = "Constellation — Whenever an enchantment you control enters, this creature gains flying until end of turn."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "78"
        artist = "Lie Setiawan"
        flavorText = "\"You can no more stop me than you can halt the tide.\""
        imageUri = "https://cards.scryfall.io/normal/front/b/8/b8cb11d7-feae-4511-9a03-bda23119b6a5.jpg"
    }
}
