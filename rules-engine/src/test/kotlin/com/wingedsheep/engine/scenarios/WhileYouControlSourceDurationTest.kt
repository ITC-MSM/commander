package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.Duration
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import com.wingedsheep.engine.core.Outcome

/**
 * Tests for [Duration.WhileYouControlSource] — the "for as long as you control this [source]"
 * floating-effect duration. Source-side mirror of [Duration.WhileControlledByController]:
 *
 *  - StateProjector drops the floating effect whenever the source leaves play, and (post-Layer-2)
 *    whenever the source's projected controller no longer matches the effect's controller.
 *  - EndedDurationExpiryCheck physically removes the floating effect on those same conditions, so
 *    regaining control of the source does NOT re-apply (CR 611.2b one-way).
 *
 * The tests use an inline `Source Control Stealer` activated-ability card that gains control of
 * target creature for `Duration.WhileYouControlSource`. A second inline `Threaten Test` sorcery
 * exercises the "opponent steals the source" case.
 */
class WhileYouControlSourceDurationTest : FunSpec({

    val projector = StateProjector()

    // {0}: "Activated: gain control of target creature for as long as you control this creature."
    val SourceControlStealer = card("Source Control Stealer") {
        manaCost = "{0}"
        typeLine = "Creature — Test"
        power = 2
        toughness = 2
        oracleText = "{T}: Gain control of target creature for as long as you control this creature."
        activatedAbility {
            cost = Costs.Tap
            val t = target("creature", Targets.Creature)
            effect = Effects.GainControl(t, Duration.WhileYouControlSource("Source Control Stealer"))
        }
    }

    // {0} sorcery: "Gain control of target permanent until end of turn." Used to flip control of
    // the *source* mid-test (Threaten-style steal of the stealer itself).
    val ThreatenAnyPermanent = card("Threaten Any Permanent") {
        manaCost = "{0}"
        typeLine = "Sorcery"
        oracleText = "Gain control of target permanent until end of turn."
        spell {
            val t = target("permanent", Targets.Permanent)
            effect = Effects.GainControl(t, Duration.EndOfTurn)
        }
    }

    val ElvishWarrior = CardDefinition.creature("Elvish Warrior Test", ManaCost.parse("{1}{G}"), emptySet(), 2, 3)

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(SourceControlStealer, ThreatenAnyPermanent, ElvishWarrior))
        return driver
    }

    /** Put the stealer on the battlefield for [owner], remove summoning sickness, and steal the
     *  given [target]. Returns the stealer's id. */
    fun GameTestDriver.activateStealOn(owner: EntityId, target: EntityId): EntityId {
        val stealer = putCreatureOnBattlefield(owner, "Source Control Stealer")
        removeSummoningSickness(stealer)
        val abilityId = SourceControlStealer.activatedAbilities.first().id
        val result = submit(
            com.wingedsheep.engine.core.ActivateAbility(
                playerId = owner,
                sourceId = stealer,
                abilityId = abilityId,
                targets = listOf(com.wingedsheep.engine.state.components.stack.ChosenTarget.Permanent(target))
            )
        )
        result.outcome shouldBe Outcome.Done
        bothPass()
        return stealer
    }

    test("Control persists for indefinite turns while you keep controlling the source") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20)
        val active = driver.activePlayer!!
        val opponent = driver.getOpponent(active)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val target = driver.putCreatureOnBattlefield(opponent, "Elvish Warrior Test")
        driver.activateStealOn(active, target)
        projector.project(driver.state).getController(target) shouldBe active

        // Advance two full turn cycles. As long as the source stays under [active]'s control the
        // gained-control effect persists.
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN, maxPasses = 600)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN, maxPasses = 600)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN, maxPasses = 600)

        projector.project(driver.state).getController(target) shouldBe active
    }

    test("Control reverts the moment the source's projected controller changes (Threaten-style)") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20)
        val active = driver.activePlayer!!
        val opponent = driver.getOpponent(active)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val target = driver.putCreatureOnBattlefield(opponent, "Elvish Warrior Test")
        val stealer = driver.activateStealOn(active, target)
        projector.project(driver.state).getController(target) shouldBe active

        // Hand priority over to the opponent and have them Threaten the stealer itself. The moment
        // the stealer's projected controller flips, the gained-control effect on `target` must drop
        // immediately — even though the stealer is still on the battlefield.
        driver.passPriorityUntil(Step.DRAW, maxPasses = 200)
        if (driver.activePlayer != opponent) driver.passPriorityUntil(Step.DRAW, maxPasses = 200)
        driver.activePlayer shouldBe opponent
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN, maxPasses = 200)

        val threaten = driver.putCardInHand(opponent, "Threaten Any Permanent")
        driver.castSpell(opponent, threaten, listOf(stealer))
        driver.bothPass()

        projector.project(driver.state).getController(stealer) shouldBe opponent
        projector.project(driver.state).getController(target) shouldBe opponent
    }

    test("CR 611.2b — regaining control of the source does NOT re-steal (one-way latch)") {
        // Same setup as above; after the Threaten wears off and the stealer returns to its
        // original controller, the previously-stolen target must STAY with its owner.
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20)
        val active = driver.activePlayer!!
        val opponent = driver.getOpponent(active)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val target = driver.putCreatureOnBattlefield(opponent, "Elvish Warrior Test")
        val stealer = driver.activateStealOn(active, target)
        projector.project(driver.state).getController(target) shouldBe active

        // Opponent steals the stealer mid-turn.
        driver.passPriorityUntil(Step.DRAW, maxPasses = 200)
        if (driver.activePlayer != opponent) driver.passPriorityUntil(Step.DRAW, maxPasses = 200)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN, maxPasses = 200)
        val threaten = driver.putCardInHand(opponent, "Threaten Any Permanent")
        driver.castSpell(opponent, threaten, listOf(stealer))
        driver.bothPass()
        projector.project(driver.state).getController(target) shouldBe opponent

        // The floating effect must be physically gone (latched off), not merely hidden by the gate.
        driver.state.floatingEffects.none {
            it.duration is Duration.WhileYouControlSource
        } shouldBe true

        // Advance to active's next main phase. EndOfTurn cleanup removes the Threaten's floating
        // effect, so the stealer returns to [active]'s control — but the gained-control of `target`
        // does NOT come back.
        driver.passPriorityUntil(Step.DRAW, maxPasses = 600)
        if (driver.activePlayer != active) driver.passPriorityUntil(Step.DRAW, maxPasses = 600)
        driver.activePlayer shouldBe active
        projector.project(driver.state).getController(stealer) shouldBe active
        projector.project(driver.state).getController(target) shouldBe opponent
    }

    test("Control reverts the moment the source is destroyed") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20)
        val active = driver.activePlayer!!
        val opponent = driver.getOpponent(active)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val target = driver.putCreatureOnBattlefield(opponent, "Elvish Warrior Test")
        val stealer = driver.activateStealOn(active, target)
        projector.project(driver.state).getController(target) shouldBe active

        // Move the stealer off the battlefield directly to its graveyard, mirroring a removal spell.
        val moved = com.wingedsheep.engine.handlers.effects.ZoneMovementUtils.moveCardToZone(
            driver.state,
            stealer,
            com.wingedsheep.sdk.core.Zone.GRAVEYARD
        )
        driver.replaceState(moved.state)

        // Source is gone — both the projection gate and the SBA latch drop the effect.
        projector.project(driver.state).getController(target) shouldBe opponent
    }
})
