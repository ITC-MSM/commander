package com.wingedsheep.mtg.sets.definitions.dsk.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CantAttackUnlessCoAttacker
import com.wingedsheep.sdk.scripting.CantBlockUnlessCoBlocker
import com.wingedsheep.sdk.scripting.ConditionalStaticAbility
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.effects.CreateTokenEffect
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Toby, Beastie Befriender
 * {2}{W}
 * Legendary Creature — Human Wizard
 * 1/1
 *
 * When Toby enters, create a 4/4 white Beast creature token with "This token can't attack or
 * block alone."
 * As long as you control four or more creature tokens, creature tokens you control have flying.
 */
val TobyBeastieBefriender = card("Toby, Beastie Befriender") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Legendary Creature — Human Wizard"
    power = 1
    toughness = 1
    oracleText = "When Toby enters, create a 4/4 white Beast creature token with \"This token can't " +
        "attack or block alone.\"\n" +
        "As long as you control four or more creature tokens, creature tokens you control have flying."

    // "can't attack or block alone" = the co-attacker restriction (existing) plus the new
    // co-blocker restriction; both filter on any creature, so any other declared attacker/blocker
    // satisfies the requirement.
    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = CreateTokenEffect(
            power = 4,
            toughness = 4,
            colors = setOf(Color.WHITE),
            creatureTypes = setOf("Beast"),
            name = "Beast",
            staticAbilities = listOf(
                CantAttackUnlessCoAttacker(coAttackerFilter = GameObjectFilter.Creature),
                CantBlockUnlessCoBlocker(coBlockerFilter = GameObjectFilter.Creature),
            ),
            imageUri = "https://cards.scryfall.io/normal/front/f/4/f43e18d8-529d-4d63-b627-b9d9fa4f3f38.jpg?1726236338",
        )
    }

    // "As long as you control four or more creature tokens, creature tokens you control have flying."
    staticAbility {
        ability = ConditionalStaticAbility(
            ability = GrantKeyword(
                Keyword.FLYING,
                GroupFilter(GameObjectFilter.Creature.youControl().token()),
            ),
            condition = Conditions.YouControlAtLeast(
                4,
                GameObjectFilter.Creature.youControl().token(),
            ),
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "35"
        artist = "Jehan Choo"
        imageUri = "https://cards.scryfall.io/normal/front/f/3/f33d3948-fe7b-4c3b-ab67-1022623fbb2b.jpg?1726285986"
    }
}
