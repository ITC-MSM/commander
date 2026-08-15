package com.wingedsheep.mtg.sets.definitions.arn.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Cuombajj Witches
 * {B}{B}
 * Creature — Human Wizard
 * 1/3
 * {T}: This creature deals 1 damage to any target and 1 damage to any target of an opponent's choice.
 *
 * The first damage targets a creature, player, planeswalker, or battle the controller chooses; the
 * second is a separate, equally-real target that an opponent chooses (the controller picks which
 * opponent in a multiplayer game). Both targets are picked at announcement — see
 * [com.wingedsheep.sdk.scripting.targets.TargetChooser] / `Targets.AnyChosenByOpponent`. Legality of
 * the opponent's target is measured relative to the controller (the ability's controller), so a
 * hexproof creature an opponent controls can't be chosen, exactly as the printed rulings state.
 */
val CuombajjWitches = card("Cuombajj Witches") {
    manaCost = "{B}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Human Wizard"
    oracleText = "{T}: This creature deals 1 damage to any target and 1 damage to any target of an opponent's choice."
    power = 1
    toughness = 3
    activatedAbility {
        cost = Costs.Tap
        val mine = target("controller target", Targets.Any)
        val theirs = target("opponent target", Targets.AnyChosenByOpponent)
        effect = Effects.DealDamage(1, mine)
            .then(Effects.DealDamage(1, theirs))
    }
    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "23"
        artist = "Kaja Foglio"
        imageUri = "https://cards.scryfall.io/normal/front/7/9/7995c3f9-a147-43c9-9f82-470924818a4c.jpg?1562917470"
        ruling("2020-11-10", "You choose which opponent chooses the second target. It doesn't have to be the opponent who controls (or is) the first target.")
        ruling("2020-11-10", "Even though the opponent chooses a target, you control the ability. An opponent can't target a creature they control with hexproof.")
    }
}
