package com.wingedsheep.mtg.sets.definitions.spm.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.ExilePatterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EventPattern.SpellCastEvent
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.TriggerSpec
import com.wingedsheep.sdk.scripting.effects.MayEffect
import com.wingedsheep.sdk.scripting.effects.MayPlayExpiry
import com.wingedsheep.sdk.scripting.references.Player

/**
 * Superior Foes of Spider-Man
 * {2}{R}
 * Creature — Human Rogue Villain, 3/3
 * Trample
 * Whenever you cast a spell with mana value 4 or greater, you may exile the top card of your
 * library. If you do, you may play that card until you exile another card with this creature.
 *
 * Modeling: a `Player.You` cast trigger gated on `manaValueAtLeast(4)` fires a `MayEffect`
 * (the "you may exile" yes/no) wrapping the standard impulse pipeline (gather top card → exile →
 * grant play-from-exile). The permission is granted with [MayPlayExpiry.Permanent] — "for as long
 * as it remains exiled" — which faithfully models the persistent, across-turns play window; the
 * grant is source-scoped to this creature via the trigger's `sourceId`.
 *
 * Deviation: the engine has no "revoke the prior grant when this same source exiles another card"
 * expiry, so the literal "until you exile another card with this creature" superseding clause is
 * approximated as `Permanent`. This diverges only in the rare case where a second mv>=4 spell is
 * cast while a card exiled by a previous trigger is still unplayed in exile — the engine keeps both
 * playable rather than revoking the earlier one. The common single-card impulse behavior matches.
 */
val SuperiorFoesOfSpiderMan = card("Superior Foes of Spider-Man") {
    manaCost = "{2}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Human Rogue Villain"
    power = 3
    toughness = 3
    oracleText = "Trample\n" +
        "Whenever you cast a spell with mana value 4 or greater, you may exile the top card of " +
        "your library. If you do, you may play that card until you exile another card with this creature."

    keywords(Keyword.TRAMPLE)

    triggeredAbility {
        trigger = TriggerSpec(
            event = SpellCastEvent(
                spellFilter = GameObjectFilter.Any.manaValueAtLeast(4),
                player = Player.You
            ),
            binding = TriggerBinding.ANY
        )
        effect = MayEffect(
            ExilePatterns.impulse(count = 1, expiry = MayPlayExpiry.Permanent)
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "96"
        artist = "Ben Harvey"
        flavorText = "Boomerang managed to assemble a squad that really wasn't all that sinister and certainly wasn't all that six."
        imageUri = "https://cards.scryfall.io/normal/front/2/8/28e7bf86-5791-4412-8184-fa63fb292be4.jpg?1783905331"
    }
}
