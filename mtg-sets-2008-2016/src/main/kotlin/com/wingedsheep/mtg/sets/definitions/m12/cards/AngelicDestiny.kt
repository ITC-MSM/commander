package com.wingedsheep.mtg.sets.definitions.m12.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.GrantSubtype
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Angelic Destiny
 * {2}{W}{W}
 * Enchantment — Aura
 * Enchant creature
 * Enchanted creature gets +4/+4, has flying and first strike, and is an Angel in addition
 * to its other types.
 * When enchanted creature dies, return this card to its owner's hand.
 *
 * The return clause moves *this Aura* (not the dead creature) — by the time the trigger
 * resolves the Aura has already been put into its owner's graveyard as a state-based action,
 * so [EffectTarget.Self] picks it up there (same shape as Spine of Ish Sah).
 */
val AngelicDestiny = card("Angelic Destiny") {
    manaCost = "{2}{W}{W}"
    colorIdentity = "W"
    typeLine = "Enchantment — Aura"
    oracleText = "Enchant creature\n" +
        "Enchanted creature gets +4/+4, has flying and first strike, and is an Angel in addition to its other types.\n" +
        "When enchanted creature dies, return this card to its owner's hand."

    auraTarget = Targets.Creature

    staticAbility {
        ability = ModifyStats(4, 4)
    }

    staticAbility {
        ability = GrantKeyword(Keyword.FLYING)
    }

    staticAbility {
        ability = GrantKeyword(Keyword.FIRST_STRIKE)
    }

    staticAbility {
        // GrantSubtype defaults its filter to the source, unlike ModifyStats/GrantKeyword
        // (which default to the attached creature) — an Aura must say so explicitly.
        ability = GrantSubtype("Angel", Filters.EnchantedCreature)
    }

    triggeredAbility {
        trigger = Triggers.leavesBattlefield(to = Zone.GRAVEYARD, binding = TriggerBinding.ATTACHED)
        effect = Effects.ReturnToHand(EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "3"
        artist = "Jana Schirmer & Johannes Voss"
        imageUri = "https://cards.scryfall.io/normal/front/a/0/a0cd7438-fde2-4e26-9c34-52c476a971e9.jpg?1783941107"
        ruling(
            "2011-09-22",
            "If the creature dies before the Angelic Destiny spell resolves, Angelic Destiny will go " +
                "to its owner's graveyard. It won't return to its owner's hand."
        )
        ruling(
            "2011-09-22",
            "If Angelic Destiny is no longer in a graveyard when its triggered ability resolves, " +
                "it won't be returned to its owner's hand."
        )
    }
}
