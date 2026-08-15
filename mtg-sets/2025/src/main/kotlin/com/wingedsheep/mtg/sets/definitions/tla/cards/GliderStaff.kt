package com.wingedsheep.mtg.sets.definitions.tla.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.ModifyStats

/**
 * Glider Staff — {2}{W} Artifact — Equipment
 *
 * When this Equipment enters, airbend up to one target creature. (Exile it. While it's exiled,
 * its owner may cast it for {2} rather than its mana cost.)
 * Equipped creature gets +1/+1 and has flying.
 * Equip {2}
 *
 * The ETB airbend uses the target-agnostic [Effects.Airbend] (it airbends the trigger's chosen
 * target via `CardSource.ChosenTargets`); the "up to one" optionality lives on the target
 * requirement. The +1/+1 and flying are two equipment statics over [Filters.EquippedCreature].
 */
val GliderStaff = card("Glider Staff") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Artifact — Equipment"
    oracleText = "When this Equipment enters, airbend up to one target creature. (Exile it. While it's exiled, its owner may cast it for {2} rather than its mana cost.)\n" +
        "Equipped creature gets +1/+1 and has flying.\n" +
        "Equip {2}"

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        target("up to one target creature", Targets.UpToCreatures(1))
        effect = Effects.Airbend()
    }

    staticAbility {
        ability = ModifyStats(1, 1, Filters.EquippedCreature)
    }

    staticAbility {
        ability = GrantKeyword(Keyword.FLYING, Filters.EquippedCreature)
    }

    equipAbility("{2}")

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "22"
        artist = "Eduardo Francisco"
        flavorText = "More tools than weapons, gliders were used by Air Nomads to harness wind currents for travel."
        imageUri = "https://cards.scryfall.io/normal/front/7/5/7517f2eb-a24d-49f6-82bf-08de55d3789a.jpg?1764120022"
    }
}
