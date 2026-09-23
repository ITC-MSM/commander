package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.handlers.effects.ZoneTransitionService
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.scripting.AbilityId
import com.wingedsheep.sdk.scripting.ActivatedAbility
import com.wingedsheep.sdk.scripting.Duration
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantActivatedAbility
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import com.wingedsheep.engine.core.Outcome

/**
 * A permanent's battlefield-scoped granted *static* ability (`GameState.grantedStaticAbilities`,
 * with no floating-effect representation) must end when the permanent leaves the battlefield
 * (CR 400.7 — a card that changes zones is a new object with no memory). Without the prune in
 * `ZoneTransitionService`, a grant like Roar of the Fifth People's chapter II ("creatures you
 * control have '{T}: Add {R}, {G}, or {W}'") lingers on the card after it hits the graveyard and
 * is still surfaced as an active-effect badge.
 */
class GrantedStaticAbilityCleanupScenarioTest : FunSpec({

    // A creature that grants itself a lasting Citanul-Hierophants static on activation.
    val granter = card("Test Static Granter") {
        manaCost = "{1}{G}"
        colorIdentity = "G"
        typeLine = "Creature — Elf"
        power = 1
        toughness = 1
        activatedAbility {
            cost = Costs.Mana("{G}")
            effect = Effects.GrantStaticAbility(
                GrantActivatedAbility(
                    ability = ActivatedAbility(
                        id = AbilityId.generate(),
                        cost = Costs.Tap,
                        effect = Effects.AddMana(Color.GREEN),
                    ),
                    filter = GroupFilter(GameObjectFilter.Creature.youControl()),
                ),
                EffectTarget.Self,
                Duration.Permanent,
            )
        }
    }

    fun resolveStack(driver: GameTestDriver) {
        var guard = 0
        while (guard++ < 40 && driver.state.stack.isNotEmpty() && !driver.isPaused) driver.bothPass()
    }

    test("a granted static ability is pruned when its holder leaves the battlefield") {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(granter))
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40), skipMulligans = true, startingPlayer = 0)
        driver.passPriorityUntil(com.wingedsheep.sdk.core.Step.PRECOMBAT_MAIN)
        val active = driver.activePlayer!!

        val g = driver.putPermanentOnBattlefield(active, "Test Static Granter")
        driver.removeSummoningSickness(g)
        driver.giveMana(active, Color.GREEN, 1)
        val abilityId = granter.activatedAbilities.first().id
        driver.submit(ActivateAbility(playerId = active, sourceId = g, abilityId = abilityId))
            .outcome shouldBe Outcome.Done
        driver.bothPass()
        resolveStack(driver)

        // The grant now lives in state, keyed to the granter.
        driver.state.grantedStaticAbilities.any { it.entityId == g } shouldBe true

        // Move it to the graveyard through the real zone-transition path.
        val result = ZoneTransitionService.moveToZone(
            state = driver.state,
            entityId = g,
            destinationZone = Zone.GRAVEYARD,
        )
        driver.replaceState(result.state)

        // The grant is gone — the card in the graveyard no longer carries it.
        driver.state.grantedStaticAbilities.any { it.entityId == g } shouldBe false
    }
})
