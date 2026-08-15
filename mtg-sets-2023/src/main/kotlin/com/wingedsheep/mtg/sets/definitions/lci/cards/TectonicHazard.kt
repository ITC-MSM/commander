package com.wingedsheep.mtg.sets.definitions.lci.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Tectonic Hazard
 * {R}
 * Sorcery
 * Tectonic Hazard deals 1 damage to each opponent and each creature they control.
 */
val TectonicHazard = card("Tectonic Hazard") {
    manaCost = "{R}"
    colorIdentity = "R"
    typeLine = "Sorcery"
    oracleText = "Tectonic Hazard deals 1 damage to each opponent and each creature they control."

    spell {
        effect = Effects.DealDamage(1, EffectTarget.PlayerRef(Player.EachOpponent))
            .then(Patterns.Group.dealDamageToAll(1, GroupFilter(GameObjectFilter.Creature.opponentControls())))
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "169"
        artist = "Jarel Threat"
        flavorText = "\"The ground is on fire, the ceiling's made of spears, and I think a mushroom tried to curse me. Don't tell me not to panic!\"\n—Jino Grag, Brazen Coalition scout"
        imageUri = "https://cards.scryfall.io/normal/front/5/2/5204c781-f568-4c7f-b3f7-ce4dd678689b.jpg?1782694475"
    }
}
