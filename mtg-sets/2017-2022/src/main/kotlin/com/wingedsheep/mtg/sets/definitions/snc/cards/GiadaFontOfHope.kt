package com.wingedsheep.mtg.sets.definitions.snc.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.AbilityCost
import com.wingedsheep.sdk.scripting.EntersWithDynamicCounters
import com.wingedsheep.sdk.scripting.EventPattern
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.effects.ManaRestriction
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Giada, Font of Hope
 * {1}{W}
 * Legendary Creature — Angel
 * 2/2
 *
 * Flying, vigilance
 * Each other Angel you control enters with an additional +1/+1 counter on it for each Angel
 * you already control.
 * {T}: Add {W}. Spend this mana only to cast an Angel spell.
 *
 * "Each Angel you already control" is the board *excluding the entering Angel* but
 * *including Giada herself* (Scryfall ruling below) — modelled as an
 * [EntersWithDynamicCounters] with `otherOnly = true` whose count is an
 * `AggregateBattlefield(excludeSelf = true)`. The global enters-with pass evaluates the count
 * with `sourceId`/`affectedEntityId` set to the *entering* permanent (the counters describe it,
 * CR 614), so `excludeSelf` drops exactly the new Angel while `Player.You` still resolves to
 * Giada's controller.
 */
val GiadaFontOfHope = card("Giada, Font of Hope") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Legendary Creature — Angel"
    oracleText = "Flying, vigilance\n" +
        "Each other Angel you control enters with an additional +1/+1 counter on it for each " +
        "Angel you already control.\n" +
        "{T}: Add {W}. Spend this mana only to cast an Angel spell."
    power = 2
    toughness = 2

    keywords(Keyword.FLYING, Keyword.VIGILANCE)

    replacementEffect(
        EntersWithDynamicCounters(
            count = DynamicAmount.AggregateBattlefield(
                player = Player.You,
                filter = GameObjectFilter.Creature.withSubtype(Subtype.ANGEL),
                excludeSelf = true,
            ),
            otherOnly = true,
            appliesTo = EventPattern.ZoneChangeEvent(
                filter = GameObjectFilter.Creature.withSubtype(Subtype.ANGEL).youControl(),
                to = Zone.BATTLEFIELD,
            ),
        )
    )

    activatedAbility {
        cost = AbilityCost.Tap
        effect = Effects.AddMana(
            Color.WHITE,
            1,
            restriction = ManaRestriction.SubtypeSpellsOnly(setOf(Subtype.ANGEL.value)),
        )
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "14"
        artist = "Eric Deschamps"
        flavorText = "The source of the Cabaretti's Halo turned out to be a single teenage girl."
        imageUri = "https://cards.scryfall.io/normal/front/b/a/bae077bd-fc8d-44d7-8c75-8dc8699c168e.jpg?1783923158"
        ruling(
            "2024-11-08",
            "\"Each Angel you already control\" means each Angel you control other than the Angel " +
                "entering, including Giada. It doesn't matter if some or all of the Angels on the " +
                "battlefield entered after Giada did."
        )
    }
}
