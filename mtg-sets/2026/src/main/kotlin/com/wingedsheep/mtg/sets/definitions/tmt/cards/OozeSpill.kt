package com.wingedsheep.mtg.sets.definitions.tmt.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Ooze Spill
 * {1}{U}{U}
 * Instant
 *
 * Counter target spell. Create a Mutagen token. (It's an artifact with
 * "{1}, {T}, Sacrifice this token: Put a +1/+1 counter on target creature.
 *  Activate only as a sorcery.")
 */
val OozeSpill = card("Ooze Spill") {
    manaCost = "{1}{U}{U}"
    colorIdentity = "U"
    typeLine = "Instant"
    oracleText = "Counter target spell. Create a Mutagen token. (It's an artifact with \"{1}, {T}, Sacrifice this token: Put a +1/+1 counter on target creature. Activate only as a sorcery.\")"

    spell {
        target = Targets.Spell
        // Counter the spell, then create the Mutagen token. The spell is the only
        // target, so if it is an illegal target on resolution (CR 608.2b) Ooze Spill
        // is removed and neither effect happens.
        effect = Effects.Composite(
            Effects.CounterSpell(),
            Effects.CreateMutagenToken()
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "48"
        artist = "Svetlin Velinov"
        flavorText = "Lab safety is everyone's business."
        imageUri = "https://cards.scryfall.io/normal/front/b/e/beef76b7-856e-48ab-bc73-e4f456c3a100.jpg?1771586813"
    }
}
