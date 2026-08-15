package com.wingedsheep.mtg.sets.definitions.vow.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.Duration
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Dominating Vampire — Innistrad: Crimson Vow #154
 * {1}{R}{R} · Creature — Vampire · Rare · 3/3
 * Artist: PINDURSKI
 *
 * When this creature enters, gain control of target creature with mana value less than or equal to
 * the number of Vampires you control until end of turn. Untap that creature. It gains haste until
 * end of turn.
 *
 * A "Threaten"-style temporary steal (Act of Treason: gain control until end of turn → untap →
 * grant haste), the one twist being the target restriction. "Mana value ≤ the number of Vampires
 * you control" is not a fixed cap but a [DynamicAmount], so it rides on the creature target's own
 * filter via [GameObjectFilter.manaValueAtMostDynamic] (Sandbender Scavengers shape) — the engine
 * re-evaluates the Vampire count at target-selection time. Dominating Vampire counts itself, so a
 * lone copy can still steal a mana-value-≤-1 creature.
 */
val DominatingVampire = card("Dominating Vampire") {
    manaCost = "{1}{R}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Vampire"
    power = 3
    toughness = 3
    oracleText = "When this creature enters, gain control of target creature with mana value less " +
        "than or equal to the number of Vampires you control until end of turn. Untap that " +
        "creature. It gains haste until end of turn."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val creature = target(
            "target creature",
            TargetCreature(
                filter = TargetFilter(
                    GameObjectFilter.Creature.manaValueAtMostDynamic(
                        DynamicAmounts.battlefield(
                            Player.You,
                            GameObjectFilter.Creature.withSubtype(Subtype.VAMPIRE)
                        ).count()
                    )
                )
            )
        )
        effect = Effects.Composite(
            Effects.GainControl(creature, Duration.EndOfTurn),
            Effects.Untap(creature),
            Effects.GrantKeyword(Keyword.HASTE, creature)
        )
        description = "When this creature enters, gain control of target creature with mana value " +
            "less than or equal to the number of Vampires you control until end of turn. Untap " +
            "that creature. It gains haste until end of turn."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "154"
        artist = "PINDURSKI"
        flavorText = "\"You can join me or feed me. Choose fast.\""
        imageUri = "https://cards.scryfall.io/normal/front/4/b/4b11d234-c6e4-45b2-a3c2-dcacc77cc084.jpg?1783924838"
    }
}
