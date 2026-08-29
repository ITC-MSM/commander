package com.wingedsheep.mtg.sets.definitions.thb.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Setessan Champion
 * {2}{G}
 * Creature — Human Warrior
 * 1/3
 *
 * Constellation — Whenever an enchantment you control enters, put a +1/+1 counter on this creature
 * and draw a card.
 *
 * Constellation is an ability word with no rules meaning of its own, so this is a plain
 * enters-the-battlefield watcher over enchantments you control with [TriggerBinding.ANY] — the
 * Champion is not an enchantment, so the trigger must not be bound to its own source. The printed
 * "and" is the two-member composite `then` builds: counter first, then the draw.
 */
val SetessanChampion = card("Setessan Champion") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Human Warrior"
    power = 1
    toughness = 3
    oracleText = "Constellation — Whenever an enchantment you control enters, put a +1/+1 counter on this creature and draw a card."

    triggeredAbility {
        trigger = Triggers.entersBattlefield(
            filter = GameObjectFilter.Enchantment.youControl(),
            binding = TriggerBinding.ANY,
        )
        effect = Effects.AddCounters("+1/+1", 1, EffectTarget.Self)
            .then(Effects.DrawCards(1))
        description = "Constellation — Whenever an enchantment you control enters, put a +1/+1 counter on this creature and draw a card."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "198"
        artist = "Emrah Elmasli"
        flavorText = "\"A blessing is not a gift. It is a duty.\""
        imageUri = "https://cards.scryfall.io/normal/front/f/8/f8ce9858-747e-441c-95a8-6af44aa2098d.jpg"
    }
}
