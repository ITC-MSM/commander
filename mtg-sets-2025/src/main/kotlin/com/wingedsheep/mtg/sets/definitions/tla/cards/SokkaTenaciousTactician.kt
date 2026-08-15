package com.wingedsheep.mtg.sets.definitions.tla.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.GrantTriggeredAbility
import com.wingedsheep.sdk.scripting.TriggeredAbility
import com.wingedsheep.sdk.scripting.effects.ModifyStatsEffect
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Sokka, Tenacious Tactician
 * {1}{U}{R}{W}
 * Legendary Creature — Human Warrior Ally
 * 3/3
 *
 * Menace, prowess (Whenever you cast a noncreature spell, this creature gets +1/+1 until end of turn.)
 * Other Allies you control have menace and prowess.
 * Whenever you cast a noncreature spell, create a 1/1 white Ally creature token.
 *
 * Prowess bundles an intrinsic +1/+1 triggered ability; the engine derives that behavior from the
 * explicit triggered ability, not the keyword tag alone (cf. Bria, Riptide Rogue). So "Other Allies
 * you control have ... prowess" grants both the keyword (display) and the prowess triggered ability
 * (behavior) to the group, while menace is a pure keyword grant.
 */
val SokkaTenaciousTactician = card("Sokka, Tenacious Tactician") {
    manaCost = "{1}{U}{R}{W}"
    colorIdentity = "URW"
    typeLine = "Legendary Creature — Human Warrior Ally"
    power = 3
    toughness = 3
    oracleText = "Menace, prowess (Whenever you cast a noncreature spell, this creature gets +1/+1 until end of turn.)\n" +
        "Other Allies you control have menace and prowess.\n" +
        "Whenever you cast a noncreature spell, create a 1/1 white Ally creature token."

    // Menace + Sokka's own prowess (keyword + intrinsic +1/+1 triggered ability).
    keywords(Keyword.MENACE)
    prowess()

    // "Other Allies you control have menace and prowess." — grant the keywords (display) plus the
    // intrinsic prowess triggered ability (behavior) to your other Allies.
    val otherAllies = GroupFilter(
        GameObjectFilter.Creature.youControl().withSubtype(Subtype.ALLY),
        excludeSelf = true,
    )
    staticAbility {
        ability = GrantKeyword(Keyword.MENACE, otherAllies)
    }
    staticAbility {
        ability = GrantKeyword(Keyword.PROWESS, otherAllies)
    }
    staticAbility {
        ability = GrantTriggeredAbility(
            ability = TriggeredAbility.create(
                trigger = Triggers.YouCastNoncreature.event,
                binding = Triggers.YouCastNoncreature.binding,
                effect = ModifyStatsEffect(
                    powerModifier = 1,
                    toughnessModifier = 1,
                    target = EffectTarget.Self,
                ),
            ),
            filter = otherAllies,
        )
    }

    // "Whenever you cast a noncreature spell, create a 1/1 white Ally creature token."
    triggeredAbility {
        trigger = Triggers.YouCastNoncreature
        effect = Effects.CreateToken(
            power = 1,
            toughness = 1,
            colors = setOf(Color.WHITE),
            creatureTypes = setOf("Ally"),
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "242"
        artist = "Robin Har"
        imageUri = "https://cards.scryfall.io/normal/front/f/0/f0fa5897-1da7-488f-bb19-1632e969c050.jpg?1764121788"
    }
}
