package com.wingedsheep.mtg.sets.definitions.ths.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Tymaret, the Murder King
 * {B}{R}
 * Legendary Creature — Zombie Warrior
 * 2 / 2
 *
 * {1}{R}, Sacrifice another creature: Tymaret deals 2 damage to target player or planeswalker.
 * {1}{B}, Sacrifice a creature: Return this card from your graveyard to your hand.
 */
val TymaretTheMurderKing = card("Tymaret, the Murder King") {
    manaCost = "{B}{R}"
    colorIdentity = "BR"
    typeLine = "Legendary Creature — Zombie Warrior"
    power = 2
    toughness = 2
    oracleText = "{1}{R}, Sacrifice another creature: Tymaret deals 2 damage to target player or planeswalker.\n{1}{B}, Sacrifice a creature: Return this card from your graveyard to your hand."

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{1}{R}"), Costs.SacrificeAnother(GameObjectFilter.Creature))
        val t = target("target", Targets.PlayerOrPlaneswalker)
        effect = Effects.DealDamage(2, t)
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{1}{B}"), Costs.Sacrifice(GameObjectFilter.Creature))
        effect = Effects.Move(EffectTarget.Self, Zone.HAND, fromZone = Zone.GRAVEYARD)
        activateFromZone = Zone.GRAVEYARD
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "207"
        artist = "Volkan Baǵa"
        flavorText = "His memories remained in the Underworld, but his cruelty crossed the Rivers with him."
        imageUri = "https://cards.scryfall.io/normal/front/4/c/4c4ce848-a57d-4e88-8093-9fb1408523bc.jpg"
    }
}
