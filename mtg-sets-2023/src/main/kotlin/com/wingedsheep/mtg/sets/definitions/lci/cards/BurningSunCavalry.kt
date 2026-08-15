package com.wingedsheep.mtg.sets.definitions.lci.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Burning Sun Cavalry
 * {1}{R}
 * Creature — Human Knight
 * 2/2
 * Whenever this creature attacks or blocks while you control a Dinosaur, this creature gets
 * +1/+1 until end of turn.
 *
 * "while you control a Dinosaur" is a trigger-time condition, NOT an intervening-if clause
 * (CR 603.4 applies only to an "if" immediately following the trigger event). Whether you control
 * a Dinosaur is checked only as this creature attacks or blocks; once the ability has triggered it
 * still resolves even if the Dinosaur later leaves. The engine's `triggerCondition` is evaluated at
 * trigger detection and not re-checked on resolution, which matches this "while" semantics.
 */
val BurningSunCavalry = card("Burning Sun Cavalry") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Human Knight"
    oracleText = "Whenever this creature attacks or blocks while you control a Dinosaur, this creature gets +1/+1 until end of turn."
    power = 2
    toughness = 2
    triggeredAbility {
        trigger = Triggers.Attacks
        triggerCondition = Conditions.YouControl(GameObjectFilter.Creature.withSubtype(Subtype.DINOSAUR))
        effect = Effects.ModifyStats(1, 1, EffectTarget.Self)
    }
    triggeredAbility {
        trigger = Triggers.Blocks
        triggerCondition = Conditions.YouControl(GameObjectFilter.Creature.withSubtype(Subtype.DINOSAUR))
        effect = Effects.ModifyStats(1, 1, EffectTarget.Self)
    }
    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "138"
        artist = "Josu Hernaiz"
        imageUri = "https://cards.scryfall.io/normal/front/0/c/0c491ac0-4752-47a7-967e-456b6e4245de.jpg?1782694499"
    }
}
