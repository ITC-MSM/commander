package com.wingedsheep.mtg.sets.definitions.dsk.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.CardLayout
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GrantKeywordToOwnSpells
import com.wingedsheep.sdk.scripting.UntapFilteredDuringOtherUntapSteps

/**
 * Dazzling Theater // Prop Room (DSK 3) — split-layout Room (CR 709.5).
 *
 * Dazzling Theater {3}{W} — Enchantment — Room
 *   Creature spells you cast have convoke.
 *
 * Prop Room {2}{W} — Enchantment — Room
 *   Untap each creature you control during each other player's untap step.
 *
 * Cast each half separately; the cast face enters unlocked, the other locked. Pay the locked
 * face's printed mana cost as a sorcery-speed special action to unlock it (CR 709.5e). Each face's
 * static ability only functions while that door is unlocked.
 *
 * Both halves reuse existing group statics: Dazzling Theater is [GrantKeywordToOwnSpells] with
 * CONVOKE over the controller's creature spells (the same "Creature spells you cast have convoke"
 * shape as Eirdu, Carrier of Dawn), and Prop Room is [UntapFilteredDuringOtherUntapSteps] over
 * creatures (the Seedborn-Muse family, filtered to creatures, cf. Ivorytusk Fortress).
 */
val DazzlingTheaterPropRoom = card("Dazzling Theater // Prop Room") {
    layout = CardLayout.SPLIT
    colorIdentity = "W"

    face("Dazzling Theater") {
        manaCost = "{3}{W}"
        typeLine = "Enchantment — Room"
        oracleText = "Creature spells you cast have convoke. (Your creatures can help cast those " +
            "spells. Each creature you tap while casting a creature spell pays for {1} or one mana " +
            "of that creature's color.)"

        staticAbility {
            ability = GrantKeywordToOwnSpells(
                keyword = Keyword.CONVOKE,
                spellFilter = Filters.Creature,
            )
        }
    }

    face("Prop Room") {
        manaCost = "{2}{W}"
        typeLine = "Enchantment — Room"
        oracleText = "Untap each creature you control during each other player's untap step."

        staticAbility {
            ability = UntapFilteredDuringOtherUntapSteps(filter = Filters.Creature)
        }
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "3"
        artist = "Henry Peters"
        imageUri = "https://cards.scryfall.io/normal/front/8/e/8e2fae80-60af-44cf-95b4-177837435d1a.jpg?1770639004"
    }
}
