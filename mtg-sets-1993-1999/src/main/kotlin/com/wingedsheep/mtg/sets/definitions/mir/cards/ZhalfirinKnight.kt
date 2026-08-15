package com.wingedsheep.mtg.sets.definitions.mir.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Zhalfirin Knight
 * {2}{W}
 * Creature — Human Knight
 * 2/2
 *
 * Flanking (Whenever a creature without flanking blocks this creature, the blocking
 * creature gets -1/-1 until end of turn.)
 * {W}{W}: This creature gains first strike until end of turn.
 *
 * The Flanking keyword on the [Keyword] enum is display-only; per-card cards in
 * Mirage block wire the trigger explicitly here so the `withoutKeyword(FLANKING)`
 * filter excludes other flanking creatures correctly.
 */
val ZhalfirinKnight = card("Zhalfirin Knight") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Knight"
    power = 2
    toughness = 2
    oracleText = "Flanking (Whenever a creature without flanking blocks this creature, " +
        "the blocking creature gets -1/-1 until end of turn.)\n" +
        "{W}{W}: This creature gains first strike until end of turn."

    keywords(Keyword.FLANKING)

    triggeredAbility {
        trigger = Triggers.becomesBlocked(
            filter = GameObjectFilter.Creature.withoutKeyword(Keyword.FLANKING)
        )
        effect = Effects.ModifyStats(-1, -1, EffectTarget.TriggeringEntity)
    }

    activatedAbility {
        cost = Costs.Mana("{W}{W}")
        effect = Effects.GrantKeyword(Keyword.FIRST_STRIKE, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "50"
        artist = "John Bolton"
        flavorText = "\"You returned a warrior. . . . Your hair was cut, your eye tattooed with the red triangle of war.\"\n—\"Love Song of Night and Day\""
        imageUri = "https://cards.scryfall.io/normal/front/e/b/eb65d104-bd50-481e-a70e-62aeb2f2c12b.jpg?1739659652"
    }
}
