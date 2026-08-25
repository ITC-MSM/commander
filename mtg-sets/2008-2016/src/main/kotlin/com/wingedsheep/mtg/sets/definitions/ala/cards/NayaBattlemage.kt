package com.wingedsheep.mtg.sets.definitions.ala.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.TapUntapEffect

/**
 * Naya Battlemage
 * {2}{G}
 * Creature — Human Shaman
 * 2 / 2
 * {R}, {T}: Target creature gets +2/+0 until end of turn.
 * {W}, {T}: Tap target creature.
 *
 * The Invasion "apprentice" shape: two independent activated abilities that share the tap symbol,
 * each gated behind an off-colour mana atom (the shard's other two colours, which is why the
 * colour identity is GRW on a mono-green cost). The first is [Effects.ModifyStats] with the default
 * end-of-turn duration; the second is [TapUntapEffect] with `tap = true`. Each names its own
 * [Targets.Creature] target.
 */
val NayaBattlemage = card("Naya Battlemage") {
    manaCost = "{2}{G}"
    colorIdentity = "GRW"
    typeLine = "Creature — Human Shaman"
    power = 2
    toughness = 2
    oracleText = "{R}, {T}: Target creature gets +2/+0 until end of turn.\n" +
        "{W}, {T}: Tap target creature."

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{R}"), Costs.Tap)
        val t = target("target", Targets.Creature)
        effect = Effects.ModifyStats(2, 0, t)
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{W}"), Costs.Tap)
        val t = target("target", Targets.Creature)
        effect = TapUntapEffect(target = t, tap = true)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "142"
        artist = "Steve Argyle"
        flavorText = "\"I have trained in all three schools of magic.\""
        imageUri = "https://cards.scryfall.io/normal/front/4/f/4fe73f62-2e80-4d5f-b7b5-c54c895a3e4d.jpg"
    }
}
