package com.wingedsheep.mtg.sets.definitions.stx.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.ModifyStats

/**
 * Poet's Quill — Strixhaven: School of Mages #82 (canonical printing)
 * {1}{B} · Artifact — Equipment
 *
 * When this Equipment enters, learn.
 * Equipped creature gets +1/+1 and has lifelink.
 * Equip {1}{B}
 *
 * The Learn is on the Equipment *entering*, not on equipping — so it happens once, when the Quill
 * is cast, and moving it between creatures later never repeats it.
 *
 * The two static riders are separate `staticAbility` blocks over [Filters.EquippedCreature]:
 * a layer-7c stat modification and a layer-6 keyword grant, which the projector applies in the
 * right order on its own (CR 613.4).
 *
 * `Learn` is [Patterns.Mechanic.learn] (CR 701.48).
 */
val PoetsQuill = card("Poet's Quill") {
    manaCost = "{1}{B}"
    colorIdentity = "B"
    typeLine = "Artifact — Equipment"
    oracleText = "When this Equipment enters, learn. (You may reveal a Lesson card you own from " +
        "outside the game and put it into your hand, or discard a card to draw a card.)\n" +
        "Equipped creature gets +1/+1 and has lifelink.\n" +
        "Equip {1}{B}"

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Patterns.Mechanic.learn()
    }

    staticAbility {
        ability = ModifyStats(+1, +1, Filters.EquippedCreature)
    }

    staticAbility {
        ability = GrantKeyword(Keyword.LIFELINK, Filters.EquippedCreature)
    }

    equipAbility("{1}{B}")

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "82"
        artist = "Anna Fehr"
        imageUri = "https://cards.scryfall.io/normal/front/e/7/e7006f43-0b10-4693-b06f-e7cf86ab4129.jpg?1783927363"
    }
}
