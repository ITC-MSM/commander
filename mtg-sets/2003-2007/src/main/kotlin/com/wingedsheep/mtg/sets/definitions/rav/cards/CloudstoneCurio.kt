package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.values.EntityReference

/**
 * Cloudstone Curio — Ravnica: City of Guilds #257
 * {3} · Artifact · Rare
 *
 * Whenever a nonartifact permanent you control enters, you may return another permanent you
 * control that shares a permanent type with it to its owner's hand.
 *
 * The bounce is a non-targeted choice made on resolution, so it is a battlefield pipeline: gather
 * every permanent you control that `sharingCardTypeWith(EntityReference.Triggering)` — both sides
 * read projected types, so an animated land that enters can bounce a creature — with
 * `excludeTriggering` carrying the printed "another", then `chooseUpTo(1)` is the "you may"
 * (zero picks is the decline) and the move routes the card to its *owner's* hand, as printed.
 * `useTargetingUI` puts the pick on the battlefield rather than in an overlay.
 *
 * "Permanent type" is spelled with the card-type predicate. The two differ only for Kindred,
 * which the Comprehensive Rules list as a card type but not as a permanent type; the Curio would let a
 * Kindred creature bounce a Kindred enchantment where the printed card would not. Nothing in the
 * SDK narrows a card-type comparison to permanent types yet, and the case is rare enough that
 * the card ships with the wider reading rather than waiting on it.
 *
 * The Curio never triggers on itself (it is an artifact) and `TriggerBinding.ANY` is what lets
 * it see the *other* permanents entering; a token entering under your control counts too.
 */
val CloudstoneCurio = card("Cloudstone Curio") {
    manaCost = "{3}"
    colorIdentity = ""
    typeLine = "Artifact"
    oracleText = "Whenever a nonartifact permanent you control enters, you may return another " +
        "permanent you control that shares a permanent type with it to its owner's hand."

    triggeredAbility {
        trigger = Triggers.entersBattlefield(
            filter = GameObjectFilter.Permanent.nonartifact().youControl(),
            binding = TriggerBinding.ANY,
        )
        effect = Effects.Pipeline {
            val candidates = gather(
                filter = GameObjectFilter.Permanent.sharingCardTypeWith(EntityReference.Triggering),
                player = Player.You,
                excludeTriggering = true,
                name = "candidates",
            )
            val returned = chooseUpTo(
                1,
                candidates,
                prompt = "You may return another permanent you control that shares a permanent " +
                    "type with the one that entered to its owner's hand",
                useTargetingUI = true,
                name = "returned",
            )
            toHand(returned)
        }
        description = "Whenever a nonartifact permanent you control enters, you may return " +
            "another permanent you control that shares a permanent type with it to its owner's hand."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "257"
        artist = "Heather Hudson"
        flavorText = "It *wants* to remain a mystery, banishing the curious in favor of less " +
            "inquisitive company."
        imageUri = "https://cards.scryfall.io/normal/front/4/7/47cbda17-d368-4dc3-b41c-95b146468b44.jpg?1783943601"
    }
}
