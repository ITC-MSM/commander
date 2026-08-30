package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.DealDamageEffect
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.ContextPropertyKey
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Coalhauler Swine
 * {4}{R}{R}
 * Creature — Boar Beast
 * 4/4
 * Whenever this creature is dealt damage, it deals that much damage to each player.
 *
 * [Triggers.TakesDamage] is the any-source "is dealt damage" trigger (combat, burn, pingers alike);
 * the amount rides the trigger context as [ContextPropertyKey.TRIGGER_DAMAGE_AMOUNT], the same way
 * Tephraderm reads it. "Each player" includes the Swine's own controller, so the payoff is a single
 * [DealDamageEffect] at [EffectTarget.PlayerRef] over [Player.Each] rather than an opponent loop.
 */
val CoalhaulerSwine = card("Coalhauler Swine") {
    manaCost = "{4}{R}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Boar Beast"
    oracleText = "Whenever this creature is dealt damage, it deals that much damage to each player."
    power = 4
    toughness = 4
    triggeredAbility {
        trigger = Triggers.TakesDamage
        effect = DealDamageEffect(
            amount = DynamicAmount.ContextProperty(ContextPropertyKey.TRIGGER_DAMAGE_AMOUNT),
            target = EffectTarget.PlayerRef(Player.Each),
        )
        description = "Whenever this creature is dealt damage, it deals that much damage to each player."
    }
    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "119"
        artist = "Daren Bader"
        flavorText = "Nothing stops industry in Ravnica—certainly not the safety of its workers."
        imageUri = "https://cards.scryfall.io/normal/front/b/c/bc001cef-3afd-4128-989f-ac99dc76b243.jpg"
    }
}
