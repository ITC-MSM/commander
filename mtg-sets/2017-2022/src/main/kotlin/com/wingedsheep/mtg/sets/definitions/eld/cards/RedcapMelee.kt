package com.wingedsheep.mtg.sets.definitions.eld.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect

/**
 * Redcap Melee — Throne of Eldraine #135 (canonical printing)
 * {R} · Instant
 *
 * Redcap Melee deals 4 damage to target creature or planeswalker. If a nonred permanent is dealt
 * damage this way, you sacrifice a land.
 *
 * Four damage for one mana, with a land sacrifice unless the thing you hit is red — which is why
 * the drawback clause reads on the *permanent's colour*, not on the spell's. The colour test is a
 * [Conditions.TargetMatchesFilter] over the same chosen target, so it reads **projected** state:
 * a creature painted red by a continuous effect (Painter's Servant, Whim of Volrath) correctly
 * spares the land, and one whose red was stripped correctly doesn't.
 *
 * The sacrifice is [Effects.SacrificeOwn] — the bare imperative "you sacrifice a land", where the
 * spell's controller chooses, not a named player.
 *
 * Ordering matters: the two sub-effects run in one [Effects.Composite], so the colour is read
 * after the damage, while the damaged permanent is still on the battlefield (a composite resolves
 * sequentially with no interleaved state-based-action pass, so a lethally damaged creature has not
 * been put into the graveyard yet and its projected colour is still readable).
 *
 * **Known deviation.** The printed trigger is "*is dealt* damage this way", so damage that is
 * prevented or replaced down to nothing should leave your lands alone. This models the clause as
 * "the target is a nonred permanent", which is the same answer in every case except an active
 * damage-prevention shield — there the card here sacrifices a land where the printed card would
 * not. Fixing it properly needs a "was this target dealt damage during this resolution" condition
 * the SDK does not have yet; it is noted here rather than silently approximated away.
 */
val RedcapMelee = card("Redcap Melee") {
    manaCost = "{R}"
    colorIdentity = "R"
    typeLine = "Instant"
    oracleText = "Redcap Melee deals 4 damage to target creature or planeswalker. If a nonred " +
        "permanent is dealt damage this way, you sacrifice a land."

    spell {
        val t = target("target creature or planeswalker", Targets.CreatureOrPlaneswalker)
        effect = Effects.DealDamage(4, t) then ConditionalEffect(
            condition = Conditions.TargetMatchesFilter(GameObjectFilter.Permanent.notColor(Color.RED)),
            effect = Effects.SacrificeOwn(Filters.Land)
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "135"
        artist = "Chris Rallis"
        flavorText = "At first, Syr Fenwick scoffed when he saw his opponents for the final match."
        imageUri = "https://cards.scryfall.io/normal/front/6/b/6bd1dd34-d480-4dfd-9f82-73c4e24a11fc.jpg?1783932619"
    }
}
