package com.wingedsheep.mtg.sets.definitions.plc.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.MayEffect

/**
 * Radha, Heir to Keld
 * {R}{G}
 * Legendary Creature — Elf Warrior
 * 2/2
 * Whenever Radha attacks, you may add {R}{R}.
 * {T}: Add {G}.
 *
 * The attack trigger is *not* a mana ability (it uses the stack — CR 605.1a excludes triggered
 * abilities that trigger off something other than mana being spent), so only the `{T}` ability
 * carries `manaAbility = true`.
 */
val RadhaHeirToKeld = card("Radha, Heir to Keld") {
    manaCost = "{R}{G}"
    colorIdentity = "RG"
    typeLine = "Legendary Creature — Elf Warrior"
    power = 2
    toughness = 2
    oracleText = "Whenever Radha attacks, you may add {R}{R}.\n" +
        "{T}: Add {G}."

    triggeredAbility {
        trigger = Triggers.Attacks
        effect = MayEffect(Effects.AddMana(Color.RED, 2))
    }

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddMana(Color.GREEN)
        manaAbility = true
        description = "{T}: Add {G}."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "162"
        artist = "Jim Murray"
        flavorText = "\"Run home, cur. I've already taken your master's head. Don't make me thrash you with it.\""
        imageUri = "https://cards.scryfall.io/normal/front/7/f/7fc23b2e-9124-4f88-b93e-8bfe90e6eb41.jpg"
    }
}
