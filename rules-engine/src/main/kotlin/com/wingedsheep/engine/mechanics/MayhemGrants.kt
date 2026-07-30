package com.wingedsheep.engine.mechanics

import com.wingedsheep.engine.state.GameState
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Single source of truth for "does this card have mayhem, and at what cost?" — used by every mayhem
 * read site (the cast-from-graveyard enumerator and the cast handler / cast-permission check).
 *
 * Mayhem (CR 702.187) can be printed on the card ([KeywordAbility.Mayhem] in the card's keyword
 * abilities) or granted at runtime to a specific card entity (Green Goblin's "Goblin Formula":
 * "Each nonland card in your graveyard has mayhem"). Routing all call sites through here keeps the
 * two sources consistent so a granted mayhem behaves identically to a printed one.
 *
 * Unlike [HarmonizeGrants], mayhem does NOT exile the spell on resolution — see [StackResolver].
 */
object MayhemGrants {

    /**
     * The effective mayhem ability for [cardId], or null if it has none. A printed mayhem on
     * [cardDef] wins; otherwise the most recently granted runtime mayhem for this entity is
     * returned (a later grant overrides an earlier one for the same card).
     */
    fun effectiveMayhem(
        state: GameState,
        cardId: EntityId,
        cardDef: CardDefinition?
    ): KeywordAbility.Mayhem? {
        cardDef?.keywordAbilities
            ?.firstOrNull { it is KeywordAbility.Mayhem }
            ?.let { return it as KeywordAbility.Mayhem }

        return state.grantedKeywordAbilities
            .lastOrNull { it.entityId == cardId && it.ability is KeywordAbility.Mayhem }
            ?.let { it.ability as KeywordAbility.Mayhem }
    }
}
