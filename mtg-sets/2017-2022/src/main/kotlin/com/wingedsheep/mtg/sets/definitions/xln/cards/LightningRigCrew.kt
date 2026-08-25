package com.wingedsheep.mtg.sets.definitions.xln.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Lightning-Rig Crew
 * {2}{R}
 * Creature — Goblin Pirate
 * 0/5
 *
 * {T}: This creature deals 1 damage to each opponent.
 * Whenever you cast a Pirate spell, untap this creature.
 */
val LightningRigCrew = card("Lightning-Rig Crew") {
    manaCost = "{2}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Goblin Pirate"
    oracleText = "{T}: This creature deals 1 damage to each opponent.\n" +
        "Whenever you cast a Pirate spell, untap this creature."
    power = 0
    toughness = 5

    triggeredAbility {
        trigger = Triggers.YouCastSubtype(Subtype.PIRATE)
        effect = Effects.Untap(EffectTarget.Self)
    }

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.DealDamage(1, EffectTarget.PlayerRef(Player.EachOpponent))
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "150"
        artist = "Filip Burburan"
        flavorText = "A pirate ship in battle is a storm of activity."
        imageUri = "https://cards.scryfall.io/normal/front/e/b/eb4a34ce-94d5-4e4d-97cc-1326cf5241f5.jpg"
    }
}
