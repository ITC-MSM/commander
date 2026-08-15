package com.wingedsheep.mtg.sets.definitions.ltr.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Gimli, Counter of Kills
 * {3}{R}
 * Legendary Creature — Dwarf Warrior
 * 4/3
 *
 * Trample
 * Whenever a creature an opponent controls dies, Gimli deals 1 damage to that
 * creature's controller.
 */
val GimliCounterOfKills = card("Gimli, Counter of Kills") {
    manaCost = "{3}{R}"
    colorIdentity = "R"
    typeLine = "Legendary Creature — Dwarf Warrior"
    power = 4
    toughness = 3
    oracleText = "Trample\nWhenever a creature an opponent controls dies, Gimli deals 1 damage to that creature's controller."

    keywords(Keyword.TRAMPLE)

    triggeredAbility {
        trigger = Triggers.leavesBattlefield(
            filter = GameObjectFilter.Creature.opponentControls(),
            to = Zone.GRAVEYARD,
            binding = TriggerBinding.ANY
        )
        effect = Effects.DealDamage(
            amount = 1,
            target = EffectTarget.ControllerOfTriggeringEntity,
            damageSource = EffectTarget.Self
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "129"
        artist = "Viko Menezes"
        flavorText = "\"Twenty-one!\" cried Gimli. He hewed a two- handed stroke and laid the last Orc before his feet. \"Now my count passes Master Legolas.\""
        imageUri = "https://cards.scryfall.io/normal/front/2/8/28b41c48-0715-49d9-98e5-e82b706da816.jpg?1686968959"
    }
}
