package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Tetsuko Umezawa, Pursuer — the ANY-bound block trigger fires once per matching blocker and binds
 * that blocker as the triggering entity, so the damage goes to the blocker's controller.
 */
val TetsukoUmezawaPursuer = card("Tetsuko Umezawa, Pursuer") {
    manaCost = "{3}{R}"
    colorIdentity = "R"
    typeLine = "Legendary Creature — Human Mercenary"
    power = 2
    toughness = 4
    oracleText = "Double strike\nProwess (Whenever you cast a noncreature spell, this creature gets +1/+1 until end of turn.)\nWhenever a creature an opponent controls with power or toughness 1 or less blocks, Tetsuko Umezawa deals 1 damage to that creature's controller."

    keywords(Keyword.DOUBLE_STRIKE)
    prowess()

    triggeredAbility {
        trigger = Triggers.blocks(
            filter = GameObjectFilter.Creature.opponentControls().powerOrToughnessAtMost(1),
            binding = TriggerBinding.ANY,
        )
        effect = Effects.DealDamage(1, EffectTarget.ControllerOfTriggeringEntity)
        description = "Whenever a creature an opponent controls with power or toughness 1 or less blocks, Tetsuko Umezawa deals 1 damage to that creature's controller."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "252"
        artist = "Tuan Duong Chu"
        flavorText = "\"Every exit is covered. You can't run.\""
        imageUri = "https://cards.scryfall.io/normal/front/d/f/df818900-ce5e-4b0d-a927-c975cbef7eda.jpg?1789128002"
        inBooster = false
    }
}
