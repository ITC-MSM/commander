package com.wingedsheep.mtg.sets.definitions.mkm.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.Duration
import com.wingedsheep.sdk.scripting.LookAtFaceDownCreatures
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Lumbering Laundry — Murders at Karlov Manor #253
 * {5} · Artifact Creature — Golem · 4/5
 *
 * {2}: Until end of turn, you may look at face-down creatures you don't control any time.
 * Disguise {5}
 *
 * The colorless disguise mirror-breaker: {2} buys x-ray vision for the turn, so you know whether the
 * 2/2 across the table is a Culvert Ambusher or a bluffed land before you attack into it.
 *
 * [LookAtFaceDownCreatures] is the same printed static Lens of Clarity carries; here it's handed out
 * for the turn by [Effects.GrantStaticAbility] anchored to the Laundry itself
 * ([EffectTarget.Self]). Visibility is a pure view concern — `Visibility.hasLookAtFaceDownCreatures`
 * asks whether the viewing player controls a permanent with the ability, and it scans
 * `GameState.grantedStaticAbilities` alongside printed ones, so a granted copy reveals exactly like
 * a printed one and the grant expires in the cleanup step.
 *
 * **Known limitation:** the grant is anchored to the Laundry, so it ends early if the Laundry leaves
 * the battlefield before end of turn. The printed card's effect is unanchored and would persist. The
 * engine has no player-anchored static grant, and the divergence only shows in the narrow line
 * "activate, then lose the Laundry, then need to look at a face-down creature the same turn."
 *
 * Disguise makes it a 2/2 with ward {2} for {3}, flipping up for {5} — worth it mostly to blank a
 * removal spell, since the 4/5 body is what you were paying {5} for anyway.
 */
val LumberingLaundry = card("Lumbering Laundry") {
    manaCost = "{5}"
    colorIdentity = ""
    typeLine = "Artifact Creature — Golem"
    power = 4
    toughness = 5
    oracleText = "{2}: Until end of turn, you may look at face-down creatures you don't control " +
        "any time.\n" +
        "Disguise {5} (You may cast this card face down for {3} as a 2/2 creature with ward {2}. " +
        "Turn it face up any time for its disguise cost.)"

    disguise = "{5}"

    activatedAbility {
        cost = Costs.Mana("{2}")
        effect = Effects.GrantStaticAbility(
            ability = LookAtFaceDownCreatures,
            target = EffectTarget.Self,
            duration = Duration.EndOfTurn,
        )
        description = "Until end of turn, you may look at face-down creatures you don't control any time."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "253"
        artist = "Michal Ivan"
        flavorText = "A true crime of fashion."
        imageUri = "https://cards.scryfall.io/normal/front/0/8/080ad039-1669-4735-9864-76f4c61fc59e.jpg?1783912828"
    }
}
