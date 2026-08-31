package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.RegenerateEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Strands of Undeath
 * {3}{B}
 * Enchantment — Aura
 *
 * Enchant creature
 * When this Aura enters, target player discards two cards.
 * {B}: Regenerate enchanted creature.
 *
 * The enters trigger targets independently of the Aura's own enchant target — the discard can be
 * pointed at any player, including the Aura's own controller, exactly as Galvanic Arc's damage
 * can. The activated ability has no mana-ability shape and no tap, so it can be activated any
 * number of times to stack regeneration shields.
 */
val StrandsOfUndeath = card("Strands of Undeath") {
    manaCost = "{3}{B}"
    colorIdentity = "B"
    typeLine = "Enchantment — Aura"
    oracleText = "Enchant creature\n" +
        "When this Aura enters, target player discards two cards.\n" +
        "{B}: Regenerate enchanted creature."

    auraTarget = Targets.Creature

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val t = target("target player", Targets.Player)
        effect = Effects.Discard(count = 2, target = t)
    }

    activatedAbility {
        cost = Costs.Mana("{B}")
        effect = RegenerateEffect(EffectTarget.EnchantedCreature)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "108"
        artist = "Dave Allsop"
        flavorText = "\"Why limit yourself to mortal law when you can outlive those who enforce " +
            "it?\"\n—Czaric, Orzhov prelate"
        imageUri = "https://cards.scryfall.io/normal/front/9/3/93ced365-f445-4a4a-b33f-986279404fde.jpg?1783943661"
    }
}
