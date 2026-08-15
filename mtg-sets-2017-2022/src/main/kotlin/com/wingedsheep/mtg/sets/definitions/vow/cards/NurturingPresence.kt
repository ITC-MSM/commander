package com.wingedsheep.mtg.sets.definitions.vow.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantTriggeredAbility
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.TriggeredAbility
import com.wingedsheep.sdk.scripting.effects.ModifyStatsEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Nurturing Presence
 * {1}{W}
 * Enchantment — Aura
 *
 * Enchant creature
 * Enchanted creature has "Whenever a creature you control enters, this creature gets +1/+1 until
 * end of turn."
 * When this Aura enters, create a 1/1 white Spirit creature token with flying.
 *
 * The quoted ability is granted to the enchanted creature via [GrantTriggeredAbility] (the Combat
 * Research / Cathar's Call shape), so "a creature **you** control" and the "this creature" self-pump
 * resolve against the enchanted creature's controller — correct even when enchanting an opponent's
 * creature. The granted trigger fires on any creature the enchanted creature's controller brings in
 * ([TriggerBinding.ANY] + `Creature.youControl()`, matching the printed "a creature you control"
 * rather than "another"); its effect is the BriaRiptideRogue self-pump
 * ([ModifyStatsEffect]`(target = EffectTarget.Self)`, default `Duration.EndOfTurn`). The one-shot
 * Spirit token is a plain self-ETB trigger on the Aura, so the token's controller is the Aura's
 * caster.
 */
val NurturingPresence = card("Nurturing Presence") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Enchantment — Aura"
    oracleText = "Enchant creature\n" +
        "Enchanted creature has \"Whenever a creature you control enters, this creature gets +1/+1 " +
        "until end of turn.\"\n" +
        "When this Aura enters, create a 1/1 white Spirit creature token with flying."

    auraTarget = Targets.Creature

    // Enchanted creature has "Whenever a creature you control enters, this creature gets +1/+1
    // until end of turn."
    val creatureEnters = Triggers.entersBattlefield(
        filter = GameObjectFilter.Creature.youControl(),
        binding = TriggerBinding.ANY
    )
    staticAbility {
        ability = GrantTriggeredAbility(
            TriggeredAbility.create(
                trigger = creatureEnters.event,
                binding = creatureEnters.binding,
                effect = ModifyStatsEffect(
                    powerModifier = 1,
                    toughnessModifier = 1,
                    target = EffectTarget.Self
                )
            )
        )
    }

    // When this Aura enters, create a 1/1 white Spirit creature token with flying.
    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.CreateToken(
            power = 1,
            toughness = 1,
            colors = setOf(Color.WHITE),
            creatureTypes = setOf("Spirit"),
            keywords = setOf(Keyword.FLYING),
            imageUri = "https://cards.scryfall.io/normal/front/6/b/6bee4081-5d74-4cc2-ba2f-887bc8799513.jpg?1783924700"
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "26"
        artist = "Irina Nordsol"
        imageUri = "https://cards.scryfall.io/normal/front/3/5/35e109ea-8b86-4432-b15e-5a6201caf2aa.jpg?1783924913"
    }
}
