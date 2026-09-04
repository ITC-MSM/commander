package com.wingedsheep.mtg.sets.definitions.roe.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Merfolk Skyscout
 * {2}{U}{U}
 * Creature — Merfolk Scout
 * 2 / 3
 *
 * Flying
 * Whenever this creature attacks or blocks, untap target permanent.
 *
 * Modeling notes:
 *  - "Attacks or blocks" is **two** triggered abilities sharing one effect. Attacking and blocking
 *    are separate events, and the SDK has no combined `AttacksOrBlocks` trigger — [Triggers.Attacks]
 *    and [Triggers.Blocks] are the two spellings, the Daemogoth Titan / Hamlet Captain / Elder
 *    Gargaroth shape. Assay's own JSON compiles this sentence into exactly two `triggeredAbilities`
 *    (`AttackEvent` and `BlockEvent`), so the pair is authoring to the model, not a workaround.
 *    Two abilities is also rules-correct: a creature that attacks and is later blocked triggers
 *    only the attack half, and each trigger picks its own target.
 *  - Each half declares its own [Targets.Permanent] requirement — the target is chosen as the
 *    ability goes on the stack (CR 603.3d), which is why it cannot be shared between the two.
 *  - "Untap target permanent" is mandatory and unconditional: no `optional`, and the target is any
 *    permanent (either controller's), matching Assay's bare `IsPermanent` predicate.
 *  - Neither half sets a `description`: the corpus leaves the paired triggers to auto-render, so
 *    the client shows one line per event rather than printing the joined sentence twice.
 */
val MerfolkSkyscout = card("Merfolk Skyscout") {
    manaCost = "{2}{U}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Merfolk Scout"
    power = 2
    toughness = 3
    oracleText = "Flying\n" +
            "Whenever this creature attacks or blocks, untap target permanent."

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.Attacks
        val permanent = target("target permanent", Targets.Permanent)
        effect = Effects.Untap(permanent)
    }

    triggeredAbility {
        trigger = Triggers.Blocks
        val permanent = target("target permanent", Targets.Permanent)
        effect = Effects.Untap(permanent)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "77"
        artist = "rk post"
        flavorText = "\"Emeria is a pleasant lie, a figment to hide Emrakul's hideous face. I can only hope to uncover a truth that lies deeper still.\""
        imageUri = "https://cards.scryfall.io/normal/front/1/c/1ce213c7-7835-4b81-a983-059dd97b0214.jpg?1783941994"
    }
}
