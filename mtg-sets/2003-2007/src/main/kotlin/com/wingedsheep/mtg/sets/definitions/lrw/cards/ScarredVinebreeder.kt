package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Scarred Vinebreeder
 * {1}{B}
 * Creature — Elf Shaman
 * 1/1
 * {2}{B}, Exile an Elf card from your graveyard: This creature gets +3/+3 until end of turn.
 */
val ScarredVinebreeder = card("Scarred Vinebreeder") {
    manaCost = "{1}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Elf Shaman"
    power = 1
    toughness = 1
    oracleText = "{2}{B}, Exile an Elf card from your graveyard: This creature gets +3/+3 until end of turn."

    activatedAbility {
        cost = Costs.Composite(
            Costs.Mana("{2}{B}"),
            Costs.ExileFromGraveyard(1, GameObjectFilter.Any.withSubtype(Subtype.ELF))
        )
        effect = Effects.ModifyStats(3, 3, EffectTarget.Self)
        description = "{2}{B}, Exile an Elf card from your graveyard: This creature gets +3/+3 until end of turn."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "138"
        artist = "Alex Horley-Orlandelli"
        flavorText = "For disfigured elves, there are few choices beyond death or nettlevine."
        imageUri = "https://cards.scryfall.io/normal/front/a/6/a60c51c2-3a24-4376-850c-cebd4d75adca.jpg?1783942884"
    }
}
