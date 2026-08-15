package com.wingedsheep.mtg.sets.definitions.atq.cards

import com.wingedsheep.sdk.core.CardType
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.Duration
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Mishra's Factory
 * Land
 *
 * {T}: Add {C}.
 * {1}: This land becomes a 2/2 Assembly-Worker artifact creature until end of turn. It's still a land.
 * {T}: Target Assembly-Worker creature gets +1/+1 until end of turn.
 *
 * The animate ability uses [Effects.BecomeCreature] with `addTypes = setOf("ARTIFACT")` so the land
 * faithfully becomes a 2/2 **artifact** creature (not merely a creature) of subtype Assembly-Worker
 * while keeping its base Land type — the type-adding floating effects are additive in the type-changing
 * layer (Layer 4), so the existing Land card type is preserved ("It's still a land"). Prior to the
 * `addTypes` addition, animate effects could only grant CREATURE, dropping the artifact type.
 * The pump ability targets any Assembly-Worker creature (including this land once animated).
 */
val MishrasFactory = card("Mishra's Factory") {
    typeLine = "Land"
    colorIdentity = ""
    oracleText = "{T}: Add {C}.\n" +
        "{1}: This land becomes a 2/2 Assembly-Worker artifact creature until end of turn. " +
        "It's still a land.\n" +
        "{T}: Target Assembly-Worker creature gets +1/+1 until end of turn."

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddColorlessMana(1)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    activatedAbility {
        cost = Costs.Mana("{1}")
        effect = Effects.BecomeCreature(
            target = EffectTarget.Self,
            power = 2,
            toughness = 2,
            creatureTypes = setOf(Subtype.ASSEMBLY_WORKER.value),
            addTypes = setOf(CardType.ARTIFACT.name),
            duration = Duration.EndOfTurn,
        )
        description = "{1}: This land becomes a 2/2 Assembly-Worker artifact creature until end of " +
            "turn. It's still a land."
    }

    activatedAbility {
        cost = Costs.Tap
        val worker = target(
            "target Assembly-Worker creature",
            TargetCreature(filter = TargetFilter(GameObjectFilter.Creature.withSubtype(Subtype.ASSEMBLY_WORKER)))
        )
        effect = Effects.ModifyStats(1, 1, worker, Duration.EndOfTurn)
        description = "{T}: Target Assembly-Worker creature gets +1/+1 until end of turn."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "80a"
        artist = "Kaja Foglio & Phil Foglio"
        imageUri = "https://cards.scryfall.io/normal/front/a/6/a696c5b6-f216-454d-8029-74e84bbd1428.jpg?1562930097"
    }
}
