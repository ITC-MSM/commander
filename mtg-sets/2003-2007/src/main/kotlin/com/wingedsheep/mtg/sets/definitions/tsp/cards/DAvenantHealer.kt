package com.wingedsheep.mtg.sets.definitions.tsp.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * D'Avenant Healer
 * {1}{W}{W}
 * Creature — Human Cleric Archer
 * 1/2
 * {T}: This creature deals 1 damage to target attacking or blocking creature.
 * {T}: Prevent the next 1 damage that would be dealt to any target this turn.
 *
 * Two tap abilities sharing one body, so only one fires per untap: the archer half is the
 * D'Avenant pinger restricted to creatures in combat ([GameObjectFilter.attackingOrBlocking]),
 * and the cleric half is a one-point [Effects.PreventNextDamage] shield on any target.
 */
val DAvenantHealer = card("D'Avenant Healer") {
    manaCost = "{1}{W}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Cleric Archer"
    power = 1
    toughness = 2
    oracleText = "{T}: This creature deals 1 damage to target attacking or blocking creature.\n" +
        "{T}: Prevent the next 1 damage that would be dealt to any target this turn."

    activatedAbility {
        cost = Costs.Tap
        val victim = target(
            "target",
            TargetCreature(filter = TargetFilter(GameObjectFilter.Creature.attackingOrBlocking())),
        )
        effect = Effects.DealDamage(1, victim)
    }

    activatedAbility {
        cost = Costs.Tap
        val shielded = target("target", Targets.Any)
        effect = Effects.PreventNextDamage(1, shielded)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "11"
        artist = "Michael Sutfin"
        flavorText = "\"One arrow keenly fired might prevent more battlefield wounds than I could treat.\""
        imageUri = "https://cards.scryfall.io/normal/front/d/e/deac6492-ce39-4137-8418-6169d3b1b632.jpg"
    }
}
