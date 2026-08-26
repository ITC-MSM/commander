package com.wingedsheep.mtg.sets.definitions.m10.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Safe Passage
 * {2}{W}
 * Instant
 *
 * Prevent all damage that would be dealt to you and creatures you control this turn.
 *
 * - A wider Fog: not combat-only and not source-restricted, so burn spells and ability damage are
 *   blanked for the rest of the turn too.
 * - [Effects.PreventAllDamageToYouAndGroup] is one recipient-group shield covering both halves —
 *   a player is not a permanent, so the "you and" rides along as
 *   [com.wingedsheep.sdk.scripting.effects.PreventDamageEffect.recipientGroupIncludesController]
 *   rather than splitting into a second effect (Eerie Interference is the same shape, narrowed to
 *   creature sources).
 * - The group is re-evaluated against projected state at the moment damage would be dealt, so a
 *   creature that changes controller mid-turn is judged as it is then.
 */
val SafePassage = card("Safe Passage") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Instant"
    oracleText = "Prevent all damage that would be dealt to you and creatures you control this turn."

    spell {
        effect = Effects.PreventAllDamageToYouAndGroup(Filters.Group.creaturesYouControl)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "28"
        artist = "Christopher Moeller"
        flavorText = "With one flap of her wings, the angel beat back the fires of war."
        imageUri = "https://cards.scryfall.io/normal/front/4/d/4d8528ef-5e7d-46da-a454-395cd38c213f.jpg?1783942398"
    }
}
