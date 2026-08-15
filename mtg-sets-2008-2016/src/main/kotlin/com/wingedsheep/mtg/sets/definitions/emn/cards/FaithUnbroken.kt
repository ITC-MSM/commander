package com.wingedsheep.mtg.sets.definitions.emn.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Faith Unbroken
 * {3}{W}
 * Enchantment — Aura
 * Enchant creature you control
 * When this Aura enters, exile target creature an opponent controls until this Aura leaves the
 * battlefield.
 * Enchanted creature gets +2/+2.
 *
 * The exile-until-leaves interaction reuses the standard [Effects.ExileUntilLeaves] /
 * [Effects.ReturnLinkedExileUnderOwnersControl] pair (Banishing Light family): the ETB links an
 * exiled opponent's creature to this Aura, and the Aura's leave trigger returns it. The +2/+2 is a
 * fixed static buff on the enchanted creature.
 */
val FaithUnbroken = card("Faith Unbroken") {
    manaCost = "{3}{W}"
    colorIdentity = "W"
    typeLine = "Enchantment — Aura"
    oracleText = "Enchant creature you control\n" +
        "When this Aura enters, exile target creature an opponent controls until this Aura leaves " +
        "the battlefield.\n" +
        "Enchanted creature gets +2/+2."

    auraTarget = Targets.CreatureYouControl

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val exiled = target("exiled", Targets.CreatureOpponentControls)
        effect = Effects.ExileUntilLeaves(exiled)
    }
    triggeredAbility {
        trigger = Triggers.LeavesBattlefield
        effect = Effects.ReturnLinkedExileUnderOwnersControl()
    }

    staticAbility {
        ability = ModifyStats(2, 2, GroupFilter.attachedCreature())
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "24"
        artist = "Kev Walker"
        imageUri = "https://cards.scryfall.io/normal/front/1/2/12bf6e58-c278-4bfe-8443-33afc7618b38.jpg?1782711933"
    }
}
